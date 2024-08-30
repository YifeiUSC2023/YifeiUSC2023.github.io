import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { PortfolioComponent } from './portfolio/portfolio.component';
import { SearchComponent } from './search/search.component';
import { WatchlistComponent } from './watchlist/watchlist.component';
const routes: Routes = [
  { path: '', redirectTo: '/search/home', pathMatch: 'full' },
  { path: 'search/home', component: HomeComponent },
  { path: 'portfolio', component: PortfolioComponent },
  { path: 'search/:ticker', component: SearchComponent },
  { path: 'watchlist', component: WatchlistComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
