import { Component } from '@angular/core';
import { Router } from '@angular/router'; // 引入 Router 服务
import { StockService } from '../stock.service';

import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith, debounceTime, switchMap,finalize } from 'rxjs/operators';
import { of } from 'rxjs';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { tap } from 'rxjs/operators';

interface Stock {
  description: string;
  displaySymbol: string;
}
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule,MatAutocompleteModule,MatInputModule,MatFormFieldModule
    ,FormsModule,ReactiveFormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  searchControl = new FormControl();
  filteredSymbols!: any;  // 这里已经是字符串数组的Observable
  currentSearch: string = '';
  isLoading: boolean = false;
  inputEmpty: boolean = false;

  constructor(private stockService: StockService, private router: Router) {} // 注入 Router

  ngOnInit(): void {

    // this.filteredSymbols = this.searchControl.valueChanges.pipe(
    //   debounceTime(300),
    //   startWith(''),
    //   switchMap(value => value ? this.stockService.getName(value) : of([]))
    // );
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
  }

  searchStock(symbol: string): void {
    // 只负责导航到 SearchComponent，并将股票代码作为参数
    // this.router.navigate(['/search', symbol]);

    this.router.navigate(['/search', symbol.toUpperCase()]);
  }
  submitSearch(symbol: string): void {
    // 导航到新的URL
    // this.router.navigate(['/search', symbol]);
    if (!symbol) {
      this.inputEmpty = true;
      return;
    }
    this.router.navigate(['/search', symbol.toUpperCase()]);
  }
  toUpperCase(input: HTMLInputElement) {
    input.value = input.value.toUpperCase();
}
// clearSearch(inputElement: HTMLInputElement): void {
//   inputElement.value = ''; // 清空输入框
//   // 这里您可以添加其他需要在清除搜索框时执行的逻辑
// }
clearSearch(): void {
  this.searchControl.setValue('');
  this.inputEmpty = false;
}

}