import { Component } from '@angular/core';
import { StockService } from '../stock.service';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
  selector: 'app-portfolio',
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.css']
})
export class PortfolioComponent {
  balance!: number;
  transactions: any[] = [];
  quantity: number = 0; // 给 quantity 一个默认值
  quantity2: number = 0;
  quoteInfo: any;
  stockInfo: any;
  selectedTicker: string = '';
  isLoading = false;
  showbuyMessage = false;
  buyMessage = '';
  showsellMessage = false;
  sellMessage = '';

  constructor(private stockService:StockService,private modalService: NgbModal,private router: Router){}

  ngOnInit(): void{
    this.isLoading = true;

    this.stockService.getBalance().subscribe(data => {
      if (data && data.length > 0) {
        this.balance = data[0].balance; // 访问数组中的第一个对象的 balance 属性
      } else {
        // 处理数据不存在的情况
        console.log('No balance data available');
      }
   });
  this.stockService.getBalance().pipe(
    finalize(() => this.isLoading = false)  // 无论如何都会执行
  ).subscribe(data => {
    if (data && data.length > 0) {
      this.balance = data[0].balance;
      this.transactions = data.filter((t: any) => t.ticker);

      if (this.transactions.length > 0) {
        const quotesObservables = this.transactions.map((transaction: any) => 
          this.stockService.getQuote(transaction.ticker).pipe(
            catchError(error => {
              console.error(`Failed to get quote for ticker ${transaction.ticker}`, error);
              return of(null);
            })
          )
        );

        forkJoin(quotesObservables).subscribe(quotes => {
          quotes.forEach((quote, index) => {
            if (quote) {
              this.transactions[index].currentPrice = quote.c;
              this.transactions[index].priceChangePercent = quote.dp;
            }
          });
        }, error => {
          console.error("An error occurred while fetching quotes", error);
        });
      } else {
        // 处理没有任何股票的情况
        console.log('No transactions data available');
      }
    } else {
      // 处理数据不存在的情况
      console.log('No balance data available');
    }
  }, error => {
    console.error("An error occurred while fetching balance", error);
  });
  


  
   
   

}

// isEnoughMoney(): boolean {
//   return (this.transactions.currentPrice * this.quantity) <= this.balance;
// }
openModal(modalContent: any) {
  this.modalService.open(modalContent);
}
buyStock(ticker: string,modal: any){
  this.selectedTicker = ticker;

  // 先调用getQuote获取当前价格等信息，因为我们需要用到当前价格
  this.stockService.getQuote(ticker).subscribe(quoteInfo => {
    this.quoteInfo = quoteInfo; // 保存获取到的报价信息
    const price = this.quoteInfo.c; // 当前价格
    const totalCost = price * this.quantity;

    // 然后，调用getStock获取股票其他信息，并执行购买逻辑
    this.stockService.getStock(ticker).subscribe(stockInfo => {
      this.stockInfo = stockInfo; // 保存获取到的股票信息

      const requestBody = {
        ticker: this.stockInfo.ticker,
        description: this.stockInfo.name,
        price: price, // 添加了价格字段
        quantity: this.quantity,
        totalCost: totalCost
      };

      // 确保在此处进行交易，确保stockInfo和quoteInfo已经定义
      this.stockService.addTransaction(requestBody).subscribe({
        next: (response) => {
          console.log(response);
           // 更新余额
          
          modal.close();
          this.buyMessage = `${this.selectedTicker} bought successfully.`
          this.showbuyMessage = true;
          setTimeout(() => this.showbuyMessage = false, 5000);
          this.balance -= totalCost;
          this.updatePortfolio();
          
        },
        error: (error) => {
          console.error("Error buying stock:", error);
          modal.close();
        }
      });
    });
  });
}
openModal2(modalContent: any) {
  this.modalService.open(modalContent);
}

sellStock(ticker: string,modal: any){
  this.selectedTicker = ticker;

  this.stockService.getQuote(ticker).subscribe(quoteInfo => {
    this.quoteInfo = quoteInfo; // 保存获取到的报价信息
    const price = this.quoteInfo.c; // 当前股票价格
    const totalCost = price * this.quantity2; // 根据用户选择的quantity2计算
    this.stockService.getStock(ticker).subscribe(stockInfo => {
      this.stockInfo = stockInfo; // 保存获取到的股票信息

      const requestBody = {
        ticker: this.stockInfo.ticker, // 假设stockInfo包含股票信息
        quantity: this.quantity2,
        price: price, // 卖出价格
      };

      this.stockService.sellStock(requestBody).subscribe({
        next: (response) => {
          console.log(response);
          this.balance += totalCost; // 卖出股票，所以是增加余额
          modal.close();
          this.sellMessage = `${this.selectedTicker} sold successfully.`
          this.showsellMessage = true;
          setTimeout(() => this.showsellMessage = false, 5000);
          this.updatePortfolio();
        },
        error: (error) => {
          console.error("Error selling stock:", error);
          modal.close();
        }
      });
    });
  });

}
updatePortfolio(): void {
  this.isLoading = true;
  this.stockService.getBalance().pipe(
    finalize(() => this.isLoading = false)  // 无论如何都会执行
  ).subscribe(data => {
    if (data && data.length > 0) {
      this.balance = data[0].balance;
      this.transactions = data.filter((t: any) => t.ticker);

      if (this.transactions.length > 0) {
        const quotesObservables = this.transactions.map((transaction: any) => 
          this.stockService.getQuote(transaction.ticker).pipe(
            catchError(error => {
              console.error(`Failed to get quote for ticker ${transaction.ticker}`, error);
              return of(null);
            })
          )
        );

        forkJoin(quotesObservables).subscribe(quotes => {
          quotes.forEach((quote, index) => {
            if (quote) {
              this.transactions[index].currentPrice = quote.c;
              this.transactions[index].priceChangePercent = quote.dp;
            }
          });
        });
      }
    }
  });
}
navigateToStockDetails(ticker: string) {
  this.router.navigate(['/search', ticker]);
}

}
