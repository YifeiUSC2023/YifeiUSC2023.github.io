from flask import Flask, request, jsonify
import requests
from datetime import datetime
from dateutil.relativedelta import relativedelta

app = Flask(__name__)

FINNHUB_API_KEY = 'cn79ohhr01qgjtj4el70cn79ohhr01qgjtj4el7g'
POLYGON_API_KEY = 'XZa_WIeYvHGkRqaTvQxtsOVs4AbeJeDx'
current_date = datetime.now().strftime('%Y-%m-%d')
date_before_30_days = (datetime.now() - relativedelta(days=30)).strftime('%Y-%m-%d')
six_months_one_day_ago = (datetime.now() - relativedelta(months=6, days=1)).strftime('%Y-%m-%d')
timespan = 'day'
multiplier = '1'



@app.route('/')
def index():
    return app.send_static_file("index.html")

@app.route('/search')
def search():
    stock_ticker = request.args.get('stock_ticker')
    result = {'error_message': None, 'company_info': None,'quote':None,'recommendation':None,'chart':None,'news':None}

    company_url = f'https://finnhub.io/api/v1/stock/profile2?symbol={stock_ticker}&token={FINNHUB_API_KEY}'
    quote_url = f'https://finnhub.io/api/v1/quote?symbol={stock_ticker}&token={FINNHUB_API_KEY}'
    quote_recommendation_url = f'https://finnhub.io/api/v1/stock/recommendation?symbol={stock_ticker}&token={FINNHUB_API_KEY}'
    news_url = f'https://finnhub.io/api/v1/company-news?symbol={stock_ticker}&from={date_before_30_days}&to={current_date}&token={FINNHUB_API_KEY}'
    chart_url = f'https://api.polygon.io/v2/aggs/ticker/{stock_ticker}/range/{multiplier}/{timespan}/{six_months_one_day_ago}/{current_date}?adjusted=true&sort=asc&apiKey={POLYGON_API_KEY}'
    
    response = requests.get(company_url)
    response2 = requests.get(quote_url)
    response3 = requests.get(quote_recommendation_url)
    response4 = requests.get(chart_url)
    response5 = requests.get(news_url)
    
    data = response.json()
    data2 = response2.json()
    data3 = response3.json()
    data4 = response4.json()
    data5 = response5.json()
    
    result['quote'] = data2
    result['recommendation'] = data3
    result['chart'] = data4
    result['news'] = data5


    if 'name' in data:
        result['company_info'] = data
    else:
          
        result['error_message'] = "Error: No record has been found, please enter a valid symbol"
    return jsonify(result)

if __name__ == '__main__':
    app.run(debug=True)