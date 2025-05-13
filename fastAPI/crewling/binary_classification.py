import joblib
import traceback
import pandas as pd
from fastapi import FastAPI
from pydantic import BaseModel
from typing import Union, Optional

app = FastAPI()

model = joblib.load('logistic_model.pkl')
vectorizer = joblib.load('vectorizer.pkl')

def predict_news_title(title: str):
    title_vec = vectorizer.transform([title])
    prediction = model.predict(title_vec)[0]
    prob = model.predict_proba(title_vec)[0][prediction]
    
    return prediction, prob


class Item(BaseModel):
    requestTyp: str
    content: Optional[str] = None


@app.get("/")
def read_root():
    return {"Hello": "World"}

@app.get("/classify")
def text_classification(title: Union[str, None] = None, contents: Union[str, None] = None):
    try:
        prediction, prob = predict_news_title(title)

        if prediction == 1:
            main_cate = '노동 관련 뉴스'
        else:
            main_cate = '노동 무관 뉴스'

        return {
            "result": "success",
            "title": title,
            "main_category": main_cate,
            "probability": f'{prob:.4f}'
        }

    except Exception as ex:
        err_msg = traceback.format_exc()
        return {
            "result": 'error',
            "msg": err_msg
        }
    