import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';


export interface SearchResult {
  result: Array<{
    description: string;
    displaySymbol: string;
  }>;
}

@Injectable({
  providedIn: 'root'
})


export class StockService {
  private baseURL = 'https://myappp-418801.uc.r.appspot.com/api/';

  constructor(private http: HttpClient) { }

  getStock(symbol: string): Observable<any> {
    return this.http.get(`${this.baseURL}stock/${symbol}`);
  }

  getQuote(symbol: string): Observable<any> {
    return this.http.get(`${this.baseURL}quote/${symbol}`);
  }

  getPolygonData(symbol: string): Observable<any> {
    return this.http.get(`${this.baseURL}polygon/${symbol}`);
  }

  getPolygonData2(symbol: string): Observable<any> {
    return this.http.get(`${this.baseURL}polygon2/${symbol}`);
  }

  
  getName(symbol: string): Observable<any> {
    return this.http.get<{result: Array<{displaySymbol: string; symbol: string; description: string; type: string}>}>(`${this.baseURL}name/${symbol}`).pipe(
      map(response => response.result
        .filter(item => item.type === 'Common Stock' && !item.symbol.includes('.'))
      )
    );
  }
  getNews(symbol: string): Observable<any> {
    return this.http.get(`${this.baseURL}news/${symbol}`);
  }
  getSentiment(symbol: string): Observable<any> {
    return this.http.get(`${this.baseURL}insider-sentiment/${symbol}`);
  }
  getRecommendation(symbol: string): Observable<any>{
    return this.http.get(`${this.baseURL}recommendation/${symbol}`);
  }
  getPeers(symbol: string): Observable<any>{
    return this.http.get(`${this.baseURL}peers/${symbol}`);
  }
  getEarnings(symbol: string): Observable<any>{
    return this.http.get(`${this.baseURL}earnings/${symbol}`);
  }
  addToFavourites(stockInfo: any): Observable<any> {
    return this.http.post(`${this.baseURL}favourites`, stockInfo);
  }
  
  removeFromFavourites(ticker: string): Observable<any> {
    return this.http.delete(`${this.baseURL}favourites/${ticker}`);
  }
  getFavourites(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseURL}favourites`);
  }
  getBalance(): Observable<any> {
    // 替换为你的后端 API 端点
    return this.http.get<any>((`${this.baseURL}transactions`));
  }
  addTransaction(transaction: any): Observable<any> {
    return this.http.post<any>(`${this.baseURL}transactions`, transaction);
}
  sellStock(transactionData: any): Observable<any> {
    return this.http.patch(`${this.baseURL}transactions`, transactionData);
}
}