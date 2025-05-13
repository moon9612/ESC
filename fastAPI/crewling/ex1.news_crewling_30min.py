import os
import time
import requests
import pandas as pd
from datetime import datetime
from bs4 import BeautifulSoup
from sqlalchemy import create_engine
from dotenv import load_dotenv
import random

urlString = 'http://qt.some.co.kr/TrendMap/JSON/ServiceHandler'
keyword = '(고용보험||임금체불||산재보험||부당해고||실업급여||근로계약||산업재해||업무상질병)&&~(#@VK#S1#스포츠)&&~(#@VK#S1#TV연예)'

def run_job():
    today = datetime.today()
    date_str = today.strftime('%Y%m%d')
    print(f"📆 자동 수집 기간: {date_str}")

    doc_list = []

    for pageNum in range(1, 2):
        params = {
            'keyword': keyword,
            'startDate': date_str,
            'endDate': date_str,
            'source': 'news',
            'lang': 'ko',
            'rowPerPage': '10',
            'pageNum': pageNum,
            'orderType': '1',
            'command': 'GetKeywordDocuments'
        }

        try:
            response = requests.post(urlString, data=params, timeout=10)
            response.raise_for_status()
            json_data = response.json()
            documents = json_data.get('item', {}).get('documentList', [])
            doc_list += documents
        except Exception as e:
            print(f"❌ 요청 or 응답 파싱 오류: {e}")
            return

        time.sleep(1)

    print(f"📄 수집된 문서 수: {len(doc_list)}")
    if not doc_list:
        print("⚠️ 문서 없음. 종료.")
        return

    df = pd.DataFrame(doc_list)

    if 'writer' in df.columns:
        writer_col = 'writer'
    elif 'writerName' in df.columns:
        writer_col = 'writerName'
    else:
        df['writer'] = '정보 없음'
        writer_col = 'writer'

    final_df = df[['title', writer_col, 'url', 'date']].copy()
    final_df.columns = ['news_title', 'news_writer', 'news_url', 'date']

    headers_list = [
        {'User-Agent': 'Mozilla/5.0'},
        {'User-Agent': 'Chrome/120.0.0.0'},
        {'User-Agent': 'Safari/537.36'},
    ]
    image_urls = []
    for i, url in enumerate(final_df['news_url']):
        try:
            headers = random.choice(headers_list)
            res = requests.get(url, headers=headers, timeout=10)
            res.raise_for_status()
            soup = BeautifulSoup(res.text, 'html.parser')
            og_image = soup.find('meta', property='og:image')
            image_url = og_image['content'] if og_image and og_image.get('content') else None
        except Exception as e:
            if i < 3:  
                print(f"❌ 이미지 추출 실패: {url} ({e})")
            image_url = None
        image_urls.append(image_url)

    final_df['news_img'] = image_urls
    final_df['crawled_at'] = datetime.now()
    final_df = final_df[['news_title', 'news_writer', 'news_img', 'news_url', 'date', 'crawled_at']]

    load_dotenv('.env')
    DB_URL = os.getenv("DB_URL")

    try:
        engine = create_engine(DB_URL)
    except Exception as e:
        print(f"❌ DB 연결 실패: {e}")
        exit()

    try:
        final_df.to_sql(name='tbl_news', con=engine, if_exists='append', index=False)
        print(f"✅ DB 저장 완료 - {len(final_df)}건")
    except Exception as e:
        print(f"❌ DB 저장 실패: {e}")

if __name__ == "__main__":
    run_job()
