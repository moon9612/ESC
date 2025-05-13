import os
import pandas as pd
from dotenv import load_dotenv
from openai import OpenAI
from sqlalchemy import create_engine
from datetime import datetime, timedelta

def run_job():
    if not load_dotenv('.env'):
        raise FileNotFoundError("❌ .env 파일을 찾을 수 없습니다. 경로를 확인하세요.")
    
    api_key = os.getenv("OPENAI_API_KEY")
    if not api_key:
        raise ValueError("❌ OPENAI_API_KEY가 .env 파일에 정의되어 있지 않습니다.")

    client = OpenAI(api_key=api_key)

    gtr_ymd = (datetime.today() - timedelta(days=1)).strftime('%Y%m%d')

    file_path = f'/result/news_{gtr_ymd}_result.csv'
    if not os.path.exists(file_path):
        print(f"⚠️ 파일 없음: {file_path}")
        return
    
    top_df = pd.read_csv(file_path)
    top_index = top_df.mdl_index.value_counts().index.tolist()

    top_list = []

    for top_idx in top_index:
        top_dict = {}
        title_list = top_df[top_df.mdl_index == top_idx]['title'].tolist()

        try:
            completion = client.chat.completions.create(
                model="gpt-4o-mini",
                messages=[
                    {
                        "role": "user",
                        "content": f"""# 입력
[뉴스 제목] = {title_list}

[질문] = 뉴스 제목 클러스터에서 공통적으로 다루는 주제를 대표할 수 있는 핵심 이슈 키워드(예: 정책명, 제도명, 핵심 공약 등)를 도출해 주세요.

# 출력
[이슈키워드] = 1개(12글자 이내, 복합명사 적극활용, 2~3개의 명사 집합도 좋음)"""
                    }
                ]
            )

            raw = completion.choices[0].message.content.strip()
            result = raw.replace('[이슈키워드] = ', '').strip()
            top_dict['mdl_index'] = top_idx
            top_dict['issue_keyword'] = result
            top_list.append(top_dict)

        except Exception as e:
            print(f"⚠️ 클러스터 {top_idx} 처리 중 오류 발생: {e}")
            continue

    if not top_list:
        print("⚠️ 추출된 이슈 키워드가 없습니다.")
        return

    df = pd.DataFrame(top_list)
    df['rnk'] = df.index + 1
    df['date'] = datetime.now()   

    df.to_csv(f'/result/keyword_{gtr_ymd}.csv', index=False, encoding='utf-8-sig')

    DB_URL = os.getenv("DB_URL")
    if not DB_URL:
        raise ValueError("❌ DB_URL 환경 변수가 설정되지 않았습니다.")

    engine = create_engine(DB_URL)

    try:
        df.to_sql(name='tbl_news_issue', con=engine, if_exists='append', index=False)
        print(f"✅ {len(df)}건 DB 저장 완료")
    except Exception as e:
        print(f"❌ DB 저장 실패: {e}")

if __name__ == "__main__":
    run_job()
