import { Component } from '@angular/core';
import { StockService } from './stock.service';
import { forkJoin } from 'rxjs';
import { RouterModule } from '@angular/router';
//import { CommonModule } from '@angular/common';
import { SearchStateService } from './search-state.service'
import { Router } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  imports: [RouterModule,NgbModule,],
 // imports: [CommonModule],
   standalone: true
})
export class AppComponent {
  title = 'MY-APP';
  stockInfo: any;
  quoteInfo: any;
  activeLink: string = 'search';
  isNavbarCollapsed = true;

  constructor(private stockService: StockService,
    private searchStateService: SearchStateService,
    private router: Router) {}

  searchStock(symbol: string): void {
    forkJoin({
      stock: this.stockService.getStock(symbol),
      quote: this.stockService.getQuote(symbol)
    }).subscribe({
      next: (results) => {
        console.log("ForkJoin results:", results);
        this.stockInfo = results.stock;
        this.quoteInfo = results.quote;
        // this.searchStateService.setLastSearch(symbol, results.stock);
      },
      error: (error) => {
        console.error('Error fetching data', error);
      }
    });
  }
  goToLastSearch() {
    const lastSearch = this.searchStateService.getLastSearch();
    if (lastSearch.ticker) {
      // 如果有上次的搜索记录，导航到搜索结果页面
      this.router.navigate(['/search', lastSearch.ticker]);
    } else {
      // 如果没有搜索记录，导航到默认的搜索页面
      this.router.navigate(['/search/home']);
    }
  }

  setActiveLink(link: string) {
    this.activeLink = link;
  }
}