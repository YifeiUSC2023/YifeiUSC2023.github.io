import { Component } from '@angular/core';
import { StockService } from '../stock.service'; // 确保路径正确
import { Router } from '@angular/router';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-watchlist',
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css'],
})
export class WatchlistComponent {
  favourites: any[] = [];
  isLoadingWatchlist: boolean = false;
  searchControl = new FormControl();

  constructor(private stockService: StockService,private router: Router) {}

  ngOnInit(): void {
    this.loadFavourites();
  }

  loadFavourites(): void {
    this.isLoadingWatchlist= true;
    this.stockService.getFavourites().subscribe({
      next: (favourites) => {
        this.isLoadingWatchlist = false;
        this.favourites = favourites;
      },
      error: (error) => {
        console.error('Error loading favourites:', error);
        this.isLoadingWatchlist = false; // 出错也需要隐藏 spinner
      }
    });
  }

  removeFavourite(ticker: string) {
    this.stockService.removeFromFavourites(ticker).subscribe({
      next: () => {
        // 数据库中的数据已删除，现在更新UI
        // 例如，从favourites数组中移除这个项目
        this.favourites = this.favourites.filter(fav => fav.stockInfo.ticker !== ticker);
        // 更新收藏按钮状态
        // 您可能需要实现逻辑以确保首页的收藏按钮状态同步更新
      },
      error: (error) => console.error('Error removing from favourites:', error)
    });
  }
  navigateToStockDetails(ticker: string) {
    this.router.navigate(['/search', ticker]);
    this.searchControl.setValue(ticker);
  }
  

}
