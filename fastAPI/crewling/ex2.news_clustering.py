import os
import time
import requests
import pandas as pd
from itertools import combinations
from collections import OrderedDict
import tomotopy as tp
from kiwipiepy import Kiwi
from konlpy.tag import Mecab
from collections import Counter
from datetime import datetime, timedelta
from sklearn.cluster import KMeans
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.feature_extraction.text import TfidfVectorizer
from sqlalchemy import create_engine
from dotenv import load_dotenv

def run_job():
    mecab = Mecab()
    kiwi = Kiwi()

    # N-Gram
    def ngram(s, num):
        res = []
        slen = len(s) - num + 1
        for i in range(slen):
            ss = s[i:i+num]
            res.append(ss)
        return res

    def diff_ngram(sa, sb, num):
        a = ngram(sa, num)
        b = ngram(sb, num)
        r = []
        cnt = 0
        for i in a:
            for j in b:
                if i == j:
                    cnt += 1
                    r.append(i)
        return cnt / len(a)

    # 토픽 단어 쿼리 추출
    def topic_combination(tr_kwd):
        del_index = set()
        for i in range(len(tr_kwd)):
            a = tr_kwd[i]

            check = 1
            for j in range(len(tr_kwd)):
                b = tr_kwd[j]
                if a == b:
                    continue
                if diff_ngram(a, b, len(a)) == 1:
                    check = 0
                    del_index.add(j)

        for index in list(reversed(sorted(list(del_index)))):
            del tr_kwd[index]
        tr_kwd = list(OrderedDict.fromkeys(tr_kwd))

        return '||'.join('(' + '&&'.join(comb) + ')' for comb in list(combinations(tr_kwd, 2)))

    tr_kwd = ['고용', '실업', '노동', '근로', '일자리', '산재', '퇴사', '해고', '임금', '체불', '보험', '산업재해', '근로계약']
    keyword = topic_combination(tr_kwd)
    keyword = '(' + keyword + ')&&~(#@VK#S1#스포츠)'

    urlString = os.getenv("urlString")

    yesterday = datetime.today() - timedelta(days=1)
    start_date = yesterday.strftime('%Y%m%d')
    end_date = yesterday.strftime('%Y%m%d')
    print(f"자동 설정된 수집 기간: {start_date} ~ {end_date}")

    doc_list = []
    for pageNum in range(1,2):  
        params = {'keyword':keyword,
                'startDate':start_date,
                'endDate':end_date,
                'source':'news', 
                'lang':'ko',
                'rowPerPage':'1000', 
                'pageNum':pageNum,
                'orderType':'1',
                'command':'GetKeywordDocuments'
                }

        response = requests.post(urlString, data=params)
        doc_list = doc_list + response.json()['item']['documentList']
        time.sleep(2)
        
    print("Total Count:", response.json()['item']['totalCnt'])
    print("Collect Data Count:", len(doc_list))

    df = pd.DataFrame(doc_list)[['date','writerName','title', 'content', 'url']] 

    gtr_ymd = (datetime.today() - timedelta(days=1)).strftime('%Y%m%d')

    df.fillna('', inplace=True)
    df['sentence'] = df[['title', 'content']].apply(" ".join, axis=1)
    df['nouns'] = df['sentence'].apply(lambda x: ', '.join(word for word in mecab.nouns(x) if len(word) > 1))

    # 텍스트를 형태소 분석하여 결과를 반환하는 함수
    def analyze_text(text):
        result = kiwi.analyze(text)
        return result

    # 형태소 분석 결과에서 명사를 추출하는 함수
    def extract_nouns(text):
        nouns = []
        result = analyze_text(text)
        for token, pos, _, _ in result[0][0]:
            if len(token) != 1 and (pos.startswith('N')):
                nouns.append(token)
        return nouns

    # LDA 토픽 뽑으면서 이슈키워드까지 출력하는 코드
    def extract_lda_topic(_df):
        corpus = _df['nouns'].str.split(', ').tolist()

        if len(corpus) >= 1000:
            k = 100
        elif len(corpus) >= 500:
            k = 70
        elif len(corpus) >= 200:
            k = 40
        else:
            k = 20

        lda_model = tp.LDAModel(tw=tp.TermWeight.PMI, min_df=3, rm_top=0, k=k, seed=572)

        for lis in corpus:
            if lis != []:
                lda_model.add_doc(lis)

        for i in range(0, 200, 4):
            lda_model.train(4, workers=1)

        word_df = pd.DataFrame()
        top_n = 10
        for i in range(lda_model.k):
            topic_words = [keyword for keyword, score in lda_model.get_topic_words(i, top_n=top_n)]
            topic_scores = [score for keyword, score in lda_model.get_topic_words(i, top_n=top_n)]
            topic_seq = list(range(len(topic_words)))
            _word_df = pd.DataFrame(data={'word_seq': topic_seq, 'word': topic_words, 'pmi_score': topic_scores})
            _word_df['topic_id'] = f'topic_{gtr_ymd}_{i}'
            # _word_df['embassy_cd'] = _df['embassy_cd'].unique()[0]
            _word_df = _word_df[['topic_id', 'word_seq', 'word', 'pmi_score']]
            word_df = pd.concat([word_df, _word_df])

        word_df = word_df.reset_index(drop=True)
        word_df = word_df[['topic_id','word_seq','word','pmi_score']]
        word_df['pmi_score'] = word_df.pmi_score.round(5)

        topic_dist = [lda_model.infer((lda_model.docs[i]))[0] for i in range(len(_df))]

        _df['max_topic_num'] = [vec.argmax() for vec in topic_dist]
        _df['max_topic_value'] = [vec.max() for vec in topic_dist] 

        use_index = _df['max_topic_num'].value_counts().index.tolist()

        topic_items = []
        for i in use_index:
            topic_dict = dict()
            topic_words = [keyword for keyword, score in lda_model.get_topic_words(i, top_n=20) if score >= 0.01]
            if len(topic_words) >= 5:
                topic_dict['mdl_index'] = i
                topic_dict['topic_words'] = ', '.join(topic_words[:5])
                topic_items.append(topic_dict)

        topic_df = pd.DataFrame(topic_items)
        if len(topic_df) == 0:
            return word_df, topic_df

        union_dict = dict()
        mdl_index_list = topic_df['mdl_index'].tolist()
        mdl_index_list.reverse()
        count = 0
        for i in mdl_index_list[1:]:
            count += 1
            target_topic_words = topic_df['topic_words'][topic_df['mdl_index'] == i].tolist()[0].split(', ')

            for j in topic_df['mdl_index'].tolist()[-count:]:
                subject_topic_words = topic_df['topic_words'][topic_df['mdl_index'] == j].tolist()[0].split(', ')
                union = set(target_topic_words) & set(subject_topic_words)
                if len(union) >= 3:
                    union_dict[j] = i

        _df['new_topic_num'] = _df['max_topic_num']
        for old, new in union_dict.items():
            _df.loc[_df['max_topic_num'] == old, 'new_topic_num'] = new

        info_df = pd.DataFrame(data={'topic_num':range(lda_model.k)})

        info_df['new_topic_num'] = info_df['topic_num']
        for old, new in union_dict.items():
            info_df.loc[info_df['topic_num'] == old, 'new_topic_num'] = new

        count_df = _df['max_topic_num'].value_counts().reset_index()
        count_df.columns = ['topic_num', 'doc_cnt']
        info_df = info_df.merge(count_df, how='left').fillna(0)
        info_df.doc_cnt = info_df.doc_cnt.astype('int')

        _df = _df[_df['max_topic_value'] >= 0.5].reset_index(drop=True)

        temp_df = _df['max_topic_num'].value_counts()
        use_index = temp_df[temp_df >= 3].index.tolist()

        _df = _df[_df['max_topic_num'].isin(use_index)].reset_index(drop=True)
        _df = _df[_df['max_topic_num'].isin(topic_df['mdl_index'].tolist())].reset_index(drop=True)

        topic_df = topic_df[topic_df['mdl_index'].isin(_df['new_topic_num'].unique().tolist())]

        _df.reset_index(inplace=True)
        _df.rename(columns={'index':'docid'}, inplace=True)

        top_df = pd.DataFrame()
        new_topic_number = 0
        for topic_number in _df['new_topic_num'].value_counts().index:
            temp_df = _df[_df['new_topic_num'] == topic_number].reset_index(drop=True)

            corpus = temp_df['nouns'].tolist()
            docid = temp_df['docid'].tolist()

            # tf-idf 적용
            tfidfv = TfidfVectorizer().fit(corpus)
            vector = tfidfv.transform(corpus).toarray()

            # k값 설정, 학습
            km = KMeans(n_clusters = 1)
            km.fit(vector)
            # 클러스터 결과로 데이터프레임 재구축
            results = []
            clusters = km.labels_.tolist()  # 군집화 결과(라벨)
            for i, value in enumerate(clusters):
                
                result_dict = {}
                result_dict['date'] = gtr_ymd
                result_dict['mdl_index'] = topic_number
                result_dict['title'] = temp_df.loc[temp_df.docid == docid[i]]['title'].tolist()[0]
                # result_dict['nouns'] = temp_df.loc[temp_df.docid == docid[i]]['nouns'].tolist()[0]
                result_dict['url'] = temp_df.loc[temp_df.docid == docid[i]]['url'].tolist()[0]
                # result_dict['topic_value'] = temp_df.loc[temp_df.docid == docid[i]]['max_topic_value'].tolist()[0]
                vec1 = vector[i].reshape(1, -1)  # 뉴스 벡터
                vec2 = km.cluster_centers_[int(value)].reshape(1,-1)  # k중앙값 벡터
                result_dict['similarity'] = cosine_similarity(vec1, vec2)[0][0]  # 코사인 유사도 계산
                results.append(result_dict)
            
            result_df = pd.DataFrame(results).sort_values(['mdl_index','similarity'], ascending=[True, False]).reset_index(drop=True)
            top_df = pd.concat([top_df, result_df])

        top_df = top_df[top_df['similarity'] >= 0.2].reset_index(drop=True)

        topic_df['doc_count'] = 0
        for mdl_index, doc_count in top_df['mdl_index'].value_counts().items():
            topic_df.loc[topic_df['mdl_index'] == mdl_index, 'doc_count'] = doc_count

        return top_df, topic_df, info_df, word_df

    top_df, topic_df, info_df, word_df = extract_lda_topic(df)
    top_df['seq'] = top_df.groupby(['date', 'mdl_index']).cumcount() + 1
    top_df = top_df[['date', 'mdl_index', 'seq', 'title', 'url', 'similarity']]
    top_index = top_df.mdl_index.value_counts().head(10).index.tolist()
    top_df = top_df[top_df.mdl_index.isin(top_index)].reset_index(drop=True)
    top_df['date'] = datetime.now()

    load_dotenv('.env')
    DB_URL = os.getenv("DB_URL")

    if not DB_URL:
        raise ValueError("❌ DB_URL 환경 변수가 설정되지 않았습니다.")

    engine = create_engine(DB_URL)
    top_df.to_sql(name='tbl_news_cluster', con=engine, if_exists='append', index=False)
    top_df.to_csv(f'/home/mentoring/result/news_{gtr_ymd}_result.csv', index=False)

if __name__ == "__main__":
    run_job()
