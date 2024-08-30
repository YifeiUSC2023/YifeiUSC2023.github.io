import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StockService } from '../stock.service';
import { forkJoin, Subscription, interval } from 'rxjs';
import { CommonModule } from '@angular/common';
// import * as Highcharts from 'highcharts';
import * as Highcharts from 'highcharts/highstock';
import { HighchartsChartModule } from 'highcharts-angular';
import { HttpClient } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import IndicatorsAll from 'highcharts/indicators/indicators-all';
import vwap from 'highcharts/indicators/vwap';
IndicatorsAll(Highcharts);
vwap(Highcharts);
import { SearchStateService } from '../search-state.service'

import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith, debounceTime, switchMap, finalize  } from 'rxjs/operators';
import { of } from 'rxjs';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { tap } from 'rxjs/operators';
// import { ChangeDetectorRef } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';


interface NewsItem {
  // headline: string;
  // image: string;
  headline: string;
  image: string;
  source: string;
  datetime: Date;
  summary: string;
  url: string; // 确保包含url属性
  
}
interface SentimentDataItem {
  positiveChange: number;
  positiveMspr: number;
  negativeChange: number;
  negativeMspr: number; 
  totalChange: number;
  totalMspr: number;
}
@Component({
  selector: 'app-search-details',
  standalone: true,
  imports: [CommonModule,HighchartsChartModule,MatAutocompleteModule,MatInputModule,MatFormFieldModule
    ,FormsModule,ReactiveFormsModule],
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit, OnDestroy {
  userSymbol: string = '';
  currentView: string = 'summary';
  stockInfo: any;
  quoteInfo: any;
  showDetails: boolean = false;
  noDataFound: boolean = false;
  inputEmpty: boolean = false;
  // 控制提示条显示的变量
  showSuccessBar = false;
  // 显示的消息内容
  successMessage = '';
  showRemoveBar = false; // 控制红色提示条是否显示
  removeMessage = ''; // 存储移除提示的消息
  showbuyMessage = false;
  buyMessage = '';
  showsellMessage = false;
  sellMessage = '';
  currentTime: Date = new Date();
  marketStatus: string = '';
  marketCloseTime: Date;
  private subscriptions = new Subscription();
  news: NewsItem[] = [];
  selectedNewsItem: NewsItem | null = null;
  peersInfo:any;
  searchQuery: string = '';
  favourites: {[key: string]: boolean} = {};
  //
  searchControl = new FormControl();
  filteredSymbols!: any; 
  currentSearch: string = '';
//
  isLoading: boolean = true;
  isLoadingSearch: boolean = false;
  balance!: number;
  quantity: number = 0; // 给 quantity 一个默认值
  quantity2: number = 0;
  foundQuantity: number = 1;
  ownedTickers: string[] = [];
  ownedStocks: any[] = [];
  ownedQuantity: number = 1;


  totalChange = 0;
  totalMspr = 0;
  totalPositiveChange = 0;
  totalPositiveMspr = 0;
  totalNegativeChange = 0;
  totalNegativeMspr = 0;
  
    // Highcharts: typeof Highcharts = Highcharts;
    // chartOptions: Highcharts.Options = {
    //   series: [{
    //     data: [1, 2, 3],
    //     type: 'line'
    //   }]
    // };
  isHighcharts = typeof Highcharts === 'object';
  Highcharts: typeof Highcharts = Highcharts;
  chartOptions: Highcharts.Options = {
    series :[{
      data:[1,2,4],
      type:'line'
    }]
  };
  chartOptions2: Highcharts.Options = {
    series :[{
      data:[1,2,4],
      type:'column'
    }]
  };
  chartOptions3: Highcharts.Options = {
    series :[{
      data:[1,2,4],
      type:'line'
    }]
  };
  chartOptions4: Highcharts.Options = {
    series :[{
      data:[1,2,4],
      type:'line'
    }]
  };
    

  constructor(
    private stockService: StockService, 
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private modalService: NgbModal,
    private searchStateService: SearchStateService,
    private cdr: ChangeDetectorRef
  ) {this.marketCloseTime = new Date();
    }

  ngOnInit(): void {

    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      tap(() => this.isLoading = true),
      switchMap(value => {
        if (value) {
          return this.stockService.getName(value).pipe(
            tap(() => this.isLoading = false), 
            finalize(() => this.isLoading = false), 
          );
        } else {
          return of([]);
        }
      })
    )
    .subscribe((data: any) => {
      this.filteredSymbols = data;
      console.log(this.filteredSymbols);
    });



    

    this.route.params.subscribe(params => {
      const symbol = params['ticker'].toUpperCase();
      this.userSymbol = symbol;

      const lastSearch = this.searchStateService.getLastSearch();
      if (lastSearch.results===symbol && lastSearch.quoteresults) {
        // 如果服务中已有搜索结果，直接使用这些结果
        console.log("lastsearch",lastSearch.quoteresults);
        console.log("lastsearch",lastSearch.results);
        this.stockInfo = lastSearch.results;
        this.quoteInfo = lastSearch.quoteresults;
        this.showDetails = true;
        this.noDataFound = false;
        this.inputEmpty = false;
        
      } else {
        // 如果服务中没有保存的搜索结果，再发起API请求
        this.searchStock(symbol);
        
      }


      

      //
      this.stockService.getPolygonData(symbol).subscribe(data => {
        // 假设 `data` 包含所需的股票价格信息
        console.log(data);
        
        let recentData = [];
        if (data && data.results && data.results.length) {
          // 仅选取最近的24个数据点
          recentData = data.results.slice(-24);
        }
        console.log(recentData);

        // 将 recentData 映射到图表需要的格式
        let d = recentData.map((item: any) => {
          return [item.t, item.c]; // 使用时间戳 `t` 和收盘价 `c`
        });

        this.chartOptions = {
          scrollbar:{
            enabled:true
          },
          title: {
            text: `${symbol} Hourly Price Variation` // 动态设置标题
          },
          legend: {
            enabled: false // 禁用图例
          },
        chart: {
          backgroundColor: '#f0f0f0', // 设置图表背景为淡灰色
          // 其他图表配置...
        },
          xAxis: {
            type: 'datetime',
            dateTimeLabelFormats: {
              hour: '%H:%M', // 每小时显示为 hh:mm
              day: '%e %b' // 跨天显示为 22 Feb
            },
            // tickInterval: 4 * 3600 * 1000, // 每3小时显示一个刻度标签
            // tickAmount: 4,
          },
          yAxis: {
            opposite: true, // 将 Y 轴放在右侧
            title: {
              text: '' // 移除y轴的标题
            }
          },
          series: [{
            type: 'line',
            data: d,
            marker: {
              enabled: false // 不显示序列的数据点
            },
            color: this.marketStatus === 'Open' ? '#00ff00' : '#ff0000'
          }]
        };
      }, error => {
        console.error('Error fetching polygon data', error);
      });
    //
      this.stockService.getRecommendation(symbol).subscribe(data=>{
        console.log(data);
        const periods = data.map((item:any) => item.period);
        const strongSellData = data.map((item: any) => item.strongSell);
        const sellData = data.map((item: any) => item.sell);
        const holdData = data.map((item: any) => item.hold);
        const buyData = data.map((item: any) => item.buy);
        const strongBuyData = data.map((item: any) => item.strongBuy);
        this.chartOptions2 = {
          chart: {
            type: 'column',
            backgroundColor: '#f0f0f0'
        },
        title: {
          text: 'Recommendation Trends', // 图表的主标题
          align: 'center', // 标题居中显示
          style: {
            fontSize: '20px' // 标题文字大小
          }
        },
          xAxis:{
            categories: periods,
          },
          yAxis: {
            min: 0,
            title: {
              text: '#Analysis'
            },
            tickInterval: 10,
          },
          plotOptions: {
            column: {
              stacking: 'normal',
              dataLabels: {
                enabled: true,
                color: 'black',
                style: {
                  textOutline: '0px',
                },
                allowOverlap: true, // 允许数据标签重叠
                formatter: function() {
                  // 使用非空断言操作符确保 this.y 不为 null 或 undefined
                  return this.y! > 0 ? this.y : '';
                }
              }
            }
          },
          series: [{
            name: 'Strong Buy',
            data: strongBuyData,
            stack: 'recommendations',
            type:'column',
            color: '#196600'
          }, {
            name: 'Buy',
            data: buyData,
            stack: 'recommendations',
            type:'column',
            color:'#009933',
          },{
            name: 'Hold',
            data: holdData,
            stack: 'recommendations',
            type:'column',
            color:'#997300'
          }, {
            name: 'Sell',
            data: sellData,
            stack: 'recommendations',
            type:'column',
            color:'#ff3333',
          },{
            name: 'Strong Sell',
            data: strongSellData,
            stack: 'recommendations',
            type:'column',
            color: '#990000',
          },  ]
        }

      }, error => {
        console.error('Error fetching polygon data', error);
      })

      this.stockService.getEarnings(symbol).subscribe(data=>{
        const periods = data.map((item: any) => item.period ?? 0);
        const actual = data.map((item: any) => item.actual ?? 0);
        const estimate = data.map((item: any) => item.estimate ?? 0);
        const surprise = data.map((item: any) => item.surprise ?? 0);

        console.log(periods,"csc");
        console.log(actual);
        console.log(estimate);

        this.chartOptions3 = {
          chart: {
            type: 'spline', // 图表类型为折线图
            backgroundColor: '#f0f0f0',
          },
          title: {
            text: 'Historical EPS Surprises' // 图表标题
          },
          xAxis: {
            categories: periods, // 使用periods数组作为x轴的类别
            labels: {
              formatter: function() {
                // 使用`<span>`标签和内联样式`text-align: center;`来居中对齐
                return '<span style="text-align: center; display: inline-block; width: 100%;">' + this.value + '<br>' + 'Surprise: ' + surprise[this.pos] + '</span>';
              },
              useHTML: true, // 允许HTML，以便可以使用<br>进行换行
            },
          },
          yAxis: {
            title: {
              text: 'Quarterly EPS' // y轴标题
            },
            tickInterval: 0.25,

          },
          series: [{
            type:'spline',
            name: 'Actual',
            data: actual // 使用actual数组作为第一个系列的数据
          }, {
            type:'spline',
            name: 'Estimate',
            data: estimate // 使用estimate数组作为第二个系列的数据
          }],
          responsive: {
            rules: [{
              condition: {
                maxWidth: 500
              },
              chartOptions: {
                legend: {
                  layout: 'horizontal',
                  align: 'center',
                  verticalAlign: 'bottom'
                }
              }
            }]
          }
        };
       



      })
      this.stockService.getPolygonData2(symbol).subscribe(data=>{
        console.log(data);

        const ohlcData = data.results.map((item: any) => [
          item.t, // 直接使用原始时间戳
          item.o,
          item.h,
          item.l,
          item.c
        ]);
        const volumeData = data.results.map((item: any) => [
          item.t, // 直接使用原始时间戳
          item.v
        ]);
        const groupingUnits: [string, number[] | null][] = [
          ['week', [1]],                         // unit name, allowed multiples
          ['month', [1, 2, 3, 4, 6]]
        ];
       
        this.chartOptions4 = {
         
          chart:{
            backgroundColor: '#f0f0f0',
           

          },
          
          navigator: {
            enabled: true, // 确保导航器启用
            
           
            
        },
          rangeSelector: {
            enabled: true,
            selected: 1, 
            buttons: [{
              type: 'month',
              count: 1,
              text: '1m'
            }, {
              type: 'month',
              count: 3,
              text: '3m'
            }, {
              type: 'month',
              count: 6,
              text: '6m'
            }, 
            {
              type: 'ytd',
              text: 'YTD'
            }, 
            {
              type: 'year',
              count: 1,
              text: '1y'
            }, {
              type: 'all',
              text: 'All'
            }],
            
          
          },

        title: {
            text:  `${symbol} Historical`
        },

        subtitle: {
            text: 'With SMA and Volume by Price technical indicators'
        },
        xAxis: {
           // 确保x轴被识别为时间轴
          dateTimeLabelFormats: {
              day: '%e %b' // “%e”表示月份中的日（没有前导零），“%b”表示月份的缩写
          },
          type: 'datetime', 
          // 可以根据需要添加其他x轴配置
      },

        yAxis: [{
            startOnTick: false,
            endOnTick: false,
            opposite:true,
            labels: {
                align: 'right',
                x: -3
            },
            title: {
                text: 'OHLC'
            },
            height: '60%',
            lineWidth: 2,
            resize: {
                enabled: true
            }
        }, {
            opposite:true,
            labels: {
                align: 'right',
                x: -3
            },
            title: {
                text: 'Volume'
            },
            top: '65%',
            height: '35%',
            offset: 0,
            lineWidth: 2
        }],tooltip: {
          split: true
      },

      plotOptions: {
          series: {
              dataGrouping: {
                  units: groupingUnits
              }
              
          }
      },
      series: [
        {
        type: 'candlestick',
        name: `${symbol}`,
        id: 'aapl',
        zIndex: 2,
        data: ohlcData
    }, {
        type: 'column',
        name: 'Volume',
        id: 'volume',
        data: volumeData,
        yAxis: 1,
        
    }, {
        type: 'vbp',
        linkedTo: 'aapl',
        params: {
            volumeSeriesID: 'volume'
        },
        dataLabels: {
            enabled: false
        },
        zoneLines: {
            enabled: false
        }
    }, {
        type: 'sma',
        linkedTo: 'aapl',
        zIndex: 1,
        marker: {
            enabled: false
        }
    }]

        }


      })

    
    });


    this.stockService.getBalance().subscribe(data => {
      if (data && data.length > 0) {
        this.balance = data[0].balance; // 访问数组中的第一个对象的 balance 属性
        this.ownedTickers = data.map((stock:any) => stock.ticker);
        this.ownedStocks = data; 
        console.log(this.ownedStocks,"股票信息")
        console.log(this.ownedTickers,"OwnedTickers")
      } else {
        // 处理数据不存在的情况
        console.log('No balance data available');
      }

      this.updateFoundQuantity();
      
      
      this.stockService.getFavourites().subscribe(favourites => {
        console.log("start check favourites");
        favourites.forEach(fav => {
          console.log("fav",fav.stockInfo.ticker);
          console.log(fav.stockInfo.ticker);
          this.favourites[fav.stockInfo.ticker] = true; // 假设后端返回的对象中包含ticker属性
        });
        // this.cdr.detectChanges(); 
        console.log(this.favourites,"favourites")
      });


    });











  
    // 初始化时立即设置定时器更新当前时间和市场状态
    this.updateTimeAndMarketStatus(); // 立即调用以显示初始状态
    const timerSubscription = interval(15000).subscribe(() => {
      this.updateTimeAndMarketStatus();
      if (this.marketStatus === 'Open' && this.stockInfo?.ticker) {
        this.updateQuoteInfo(this.stockInfo.ticker);
      }
    });
    this.subscriptions.add(timerSubscription);

   
  }

  ngOnDestroy(): void {
    // 清理订阅
    this.subscriptions.unsubscribe();
  }

  updateTimeAndMarketStatus(): void {
    this.currentTime = new Date(); // 更新当前时间
    if (this.quoteInfo && this.quoteInfo.t) {
      const marketTime = new Date(this.quoteInfo.t * 1000);
      this.marketCloseTime = marketTime; // 存储市场时间
      const diffMinutes = (this.currentTime.getTime() - marketTime.getTime()) / (1000 * 60);
      this.marketStatus = diffMinutes > 5 ? 'Closed' : 'Open';
    }
  }
  

  searchStock(symbol: string): void {
    if (!symbol.trim()) {
      // 如果输入为空或只包含空格
      this.inputEmpty = true; // 显示输入为空的错误提示
      this.noDataFound = false; // 确保不显示无数据的错误提示
      this.showDetails = false;
      return; // 不执行后续的API调用
    }
    this.searchControl.setValue(symbol);
    this.isLoadingSearch = true;
    this.inputEmpty = false; // 确保不显示输入为空的错误提示
    this.noDataFound = false; // 重置无数据的错误提示，以备后续使用
    
    this.updateFoundQuantity();
    this.stockService.getStock(symbol).subscribe(stockResult => {
      this.isLoadingSearch = false;
      if (stockResult?.ticker) {
        this.stockInfo = stockResult;
        // this.searchStateService.setLastSearch(symbol, stockResult,);
        // 初次加载时更新 quoteInfo 之后立即更新市场状态
        this.updateQuoteInfo(symbol);
      } else {
        this.showDetails = false;
        this.noDataFound = true;
        this.isLoadingSearch = false;
        this.showDetails = false;
        this.isHighcharts = false;
      }
    }, error => {
      console.error('Error fetching stock info', error);
      this.noDataFound = true;
      this.isLoadingSearch = false;
      this.showDetails = false;
      this.isHighcharts = false;
    });
    this.getpeer(this.userSymbol);
  }

  getpeer(symbol: string): void {
    this.stockService.getPeers(symbol).subscribe(peerResult => {
      this.peersInfo = peerResult;
      console.log(this.peersInfo);
    })
    
  }

  updateQuoteInfo(symbol: string): void {
    this.stockService.getQuote(symbol).subscribe(quoteResult => {
      if (quoteResult?.c !== undefined) {
        this.quoteInfo = quoteResult;
        this.showDetails = true;
        this.noDataFound = false;
        this.searchStateService.setLastSearch(symbol, this.stockInfo,quoteResult);
        this.updateTimeAndMarketStatus(); // 获取到新的报价信息后更新市场状态
      } else {
        this.noDataFound = true; // 保持 noDataFound 状态更新逻辑
      }
    }, error => {
      console.error('Error updating quote info', error);
      this.noDataFound = true;
    });
  }

  shownews(symbol: string): void {
    this.stockService.getNews(symbol.toUpperCase()).subscribe(newsResult => {
      // 在这里转换每个新闻项的datetime属性
      const validNews = newsResult.map((item: any) => ({
        ...item,
        datetime: new Date(item.datetime * 1000) // 将datetime从秒转换为毫秒
      }))
      .filter((item: NewsItem) => item.headline && item.image)
      .slice(0, 20);
  
      this.news = validNews;
      console.log(this.news);
    }, error => {
      console.error('Error fetching news:', error);
    });
  }


  calculateSum(symbol: string): void {
    this.stockService.getSentiment(symbol.toUpperCase()).subscribe(newsResult => {
      // 初始化求和变量
      let positiveChange = 0;
      let negativeChange = 0;
      let totalChange = 0;

      let positiveMspr = 0;
      let negativeMspr = 0;
      let totalMspr = 0;
  
      // 检查newsResult.data是否存在并且是数组
      if (newsResult.data && Array.isArray(newsResult.data)) {
        // 遍历数组
        newsResult.data.forEach((item:any) => {
          // 假设每个item都有一个名为change的属性
          const change = item.change;
          totalChange += change; // 累加到总和
          if (change > 0) {
            positiveChange += change; // 正change累加到正和
          } else if (change < 0) {
            negativeChange += change; // 负change累加到负和
          }

          const Mspr = item.mspr;
          totalMspr += Mspr; // 累加到总和
          if (change > 0) {
            positiveMspr += Mspr; // 正change累加到正和
          } else if (change < 0) {
            negativeMspr += Mspr; // 负change累加到负和
          }
        });
      }

      this.totalChange = totalChange;
      this.totalMspr = totalMspr;
      this.totalPositiveChange = positiveChange;
      this.totalPositiveMspr = positiveMspr;
      this.totalNegativeChange = negativeChange;
      this.totalNegativeMspr = negativeMspr;
  
     
    });
  }


  openModal(content: any, newsItem: NewsItem): void {
    this.selectedNewsItem = newsItem;
    this.modalService.open(content);
  }

  openModal2(modalContent: any) {
    this.modalService.open(modalContent);
  }
  openModal3(modalContent: any) {
    this.modalService.open(modalContent);
  }
  getTwitterShareLink(): string {
    const text = encodeURIComponent(`${this.selectedNewsItem?.headline}`);
    const url = encodeURIComponent(`${this.selectedNewsItem?.url}`);
    return `https://twitter.com/intent/tweet?text=${text}&url=${url}`;
  }

  getFacebookShareLink(): string {
    const currentUrl = encodeURIComponent(`${this.selectedNewsItem?.url}`);
    return `https://www.facebook.com/sharer/sharer.php?u=${currentUrl}`;
  }
  
  changeView(newView: string) {
    this.currentView = newView;
  }

  handleTopNewsClick(): void {
    this.changeView('topnews'); // 切换视图
    this.shownews(this.userSymbol); // 加载新闻
  }

  handleInsightsClick(): void{
    this.changeView('insight');
    this.calculateSum(this.userSymbol);
  }

  submitSearch(symbol: string): void {
    this.isLoadingSearch = true;
    if (!symbol) {
      this.noDataFound = false;
      this.showDetails = false;
      this.isHighcharts = false; 
      this.inputEmpty = true;
      this.isLoadingSearch = false;
      return;
    }
    this.searchControl.setValue(this.userSymbol);
    this.searchQuery = this.userSymbol;
    this.router.navigate(['/search', symbol]);
    // this.changeView('summary');
    
    this.searchStock(symbol);
    this.isLoadingSearch = false;
    
    
  }


  toggleFavourite(stockInfo: any, quoteInfo: any) {
    // const isFavourite = !this.favourites[stockInfo.ticker];
    // this.favourites[stockInfo.ticker] = isFavourite;

    const favouriteData = { stockInfo, quoteInfo };
    const isFavourite = !this.favourites[stockInfo.ticker];
    this.favourites[stockInfo.ticker] = isFavourite;
  
    if (isFavourite) {
      // // 添加到数据库
      this.stockService.addToFavourites(favouriteData).subscribe({
        // next: (response) => console.log('Added to favourites:', response),
        // error: (error) => console.error('Error adding to favourites:', error)
        next: (response) => {
          console.log('Added to favourites:', response);
          // 显示提示条并设置消息
          this.successMessage = `${stockInfo.ticker} added to Watchlist`;
          this.showSuccessBar = true;
          // 可以设置几秒后自动隐藏
          setTimeout(() => this.showSuccessBar = false, 5000); // 3秒后隐藏
        },
        error: (error) => {
          console.error('Error adding to favourites:', error);
          this.showSuccessBar = false;
        }
      });
    } else {
      // 删除数据
      this.stockService.removeFromFavourites(stockInfo.ticker).subscribe({
        // next: (response) => console.log('Removed from favourites:', response),
        // error: (error) => console.error('Error removing from favourites:', error)
        next: (response) => {
          console.log('Removed from favourites:', response);
          // 显示红色提示条并设置消息
          this.removeMessage = `${stockInfo.ticker} removed from Watchlist`;
          this.showRemoveBar = true;
          // 可以设置几秒后自动隐藏
          setTimeout(() => this.showRemoveBar = false, 5000); // 5秒后隐藏
        },
        error: (error) => {
          console.error('Error removing from favourites:', error);
          this.showRemoveBar = false;
        }
      });
    }
  }




  
  




  toUpperCase(input: HTMLInputElement) {
    input.value = input.value.toUpperCase();
}
// clearSearch(inputElement: HTMLInputElement): void {
//   inputElement.value = ''; // 清空输入框
//   // 这里您可以添加其他需要在清除搜索框时执行的逻辑
//   this.noDataFound = false;
//   this.inputEmpty = false;
//   this.showDetails = false;
//   this.isHighcharts = false;
// }


clearSearch(): void {
  this.searchControl.setValue('');
  this.noDataFound = false;
  this.inputEmpty = false;
  this.showDetails = false;
  this.isHighcharts = false;
  this.showSuccessBar = false;
  this.showRemoveBar = false;
  this.showbuyMessage = false;
  this.showsellMessage = false;
}


isEnoughMoney(): boolean {
  return (this.quoteInfo?.c * this.quantity) <= this.balance;
}


buyStock(modal: any): void {
  const price = this.quoteInfo.c; // 当前价格
  const totalCost = price * this.quantity;

  const requestBody = {
    ticker: this.stockInfo.ticker,
    description: this.stockInfo.name,
    price: price, // 添加了价格字段
    quantity: this.quantity,
    totalCost: totalCost
  };

  this.stockService.addTransaction(requestBody).subscribe({
    next: (response) => {
      console.log(response);
      
      
      this.stockService.getBalance().subscribe(data => {

        modal.close();
        this.ownedStocks = data; 
        this.ownedTickers = data.map((stock:any) => stock.ticker);
        this.updateFoundQuantity();
        
        
        this.buyMessage = `${this.stockInfo.ticker} bought successfully.`
        this.showbuyMessage = true;
        setTimeout(() => this.showbuyMessage = false, 5000);
        this.cdr.detectChanges();
        
      });
      this.balance -= totalCost;
    
    },
    error: (error) => {
      console.error("Error buying stock:", error);
      modal.close();
      this.showbuyMessage = false;
    }
  });
  
  
}

sellStock(modal: any): void {
  const price = this.quoteInfo.c; // 当前股票价格
  const totalCost = price * this.quantity2; // 根据用户选择的quantity2计算

  const requestBody = {
    ticker: this.stockInfo.ticker, // 假设stockInfo包含股票信息
    quantity: this.quantity2,
    price: price, // 卖出价格
  };

  this.stockService.sellStock(requestBody).subscribe({
    next: (response) => {
      console.log(response);
       // 卖出股票，所以是增加余额
      this.stockService.getBalance().subscribe(data => {

      modal.close();
      this.ownedStocks = data; 
      this.ownedTickers = data.map((stock:any) => stock.ticker);
      this.updateFoundQuantity();
      this.sellMessage = `${this.stockInfo.ticker} sold successfully.`
      this.showsellMessage = true;
      setTimeout(() => this.showsellMessage = false, 5000);
      this.cdr.detectChanges();
        
      });
      this.balance += totalCost;
      
    },
    error: (error) => {
      console.error("Error selling stock:", error);
      modal.close();
    }
  });
  
}









updateFoundQuantity() {
  const foundStock = this.ownedStocks.find(stock => stock.ticker === this.userSymbol);

  if (foundStock) {
    this.foundQuantity = foundStock.quantity;
    console.log(foundStock.quantity, "买的这个公司股票的数量");
  } else {
    console.log("没找到");
    this.foundQuantity = 0;
  }
}










  
}