import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SearchStateService {
  private lastSearch = {
    ticker: '',
    results: null, // 假设这是您搜索API返回的数据类型，根据实际情况调整
    quoteresults:null
  };

  constructor() { }

  setLastSearch(ticker: string, results: any,quoteresults:any) {
    console.log("setlast",ticker,results,quoteresults);
    this.lastSearch = { ticker, results,quoteresults };
  }

  getLastSearch() {
    
    return this.lastSearch;
    
  }
}