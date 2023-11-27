import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { DialogConfirmationComponent } from 'src/app/core/dialog-confirmation/dialog-confirmation.component';
import { Pageable } from 'src/app/core/model/page/Pageable';
import { LoanEditComponent } from '../loan-edit/loan-edit.component';
import { LoanService } from '../loan.service';
import { Loan } from '../model/Loan';
import { Game } from 'src/app/game/model/Game';
import { Client } from 'src/app/client/model/Client';
import { ClientService } from 'src/app/client/client.service';
import { GameService } from 'src/app/game/game.service';

@Component({
  selector: 'app-loan-list',
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.scss']
})
export class LoanListComponent implements OnInit {

  pageNumber: number = 0;
  pageSize: number = 5;
  totalElements: number = 0;

  dataSource = new MatTableDataSource<Loan>();
  displayedColumns: string[] = ['id', 'game.title', 'client.name', 'startDate', 'endDate', 'action' ];

  loan: Loan[];
  clients: Client[];
  games: Game[];

  filterGame : Game;
  filterClient: Client;
  filterDate: Date; 

  constructor(
    private loanService: LoanService,
    private clientService: ClientService,
    private gameService: GameService,
    public dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.loadPage();
    this.loadGames();
    this.loadClients();
    
  }
  loadPage(event?: PageEvent) {

    let pageable : Pageable =  {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: [{
            property: 'id',
            direction: 'ASC'
        }]
    }

    if (event != null) {
        pageable.pageSize = event.pageSize
        pageable.pageNumber = event.pageIndex;
    }

    this.loanService.getLoans(pageable).subscribe(data => {
        this.dataSource.data = data.content;
        this.pageNumber = data.pageable.pageNumber;
        this.pageSize = data.pageable.pageSize;
        this.totalElements = data.totalElements;
    });

}  

loadGames(): void {
  this.gameService.getGames().subscribe(
    games => {
      this.games = games;
    }
  );
}

loadClients(): void {
  this.clientService.getClients().subscribe(
    clients => {
      this.clients = clients;
    }
  );
}

createLoan() {      
  const dialogRef = this.dialog.open(LoanEditComponent, {
      data: {}
  });

  dialogRef.afterClosed().subscribe(result => {
      this.ngOnInit();
  });      
}  

editLoan(loan: Loan) {    
  const dialogRef = this.dialog.open(LoanEditComponent, {
      data: { loan: loan }
  });

  dialogRef.afterClosed().subscribe(result => {
      this.ngOnInit();
  });    
}


deleteLoan(loan: Loan) {    
  const dialogRef = this.dialog.open(DialogConfirmationComponent, {
      data: { title: "Eliminar prestamo", description: "Atención si borra el prestamo se perderán sus datos.<br> ¿Desea eliminar el autor?" }
  });

  dialogRef.afterClosed().subscribe(result => {
      if (result) {
          this.loanService.deleteLoan(loan.id).subscribe(result =>  {
              this.ngOnInit();
          }); 
      }
  });
} 

onCleanFilter(): void {
  this.filterGame = null;
  this.filterClient = null;
  this.filterDate = null;

  this.onSearch();
}

onSearch(): void {
  
  let pageable: Pageable = {
    pageNumber: this.pageNumber,
    pageSize: this.pageSize,
    sort: [{
      property: 'id',
      direction: 'ASC'
    }]
  };

  let titleGame = this.filterGame ? this.filterGame.title : null;
  let nameClient = this.filterClient ? this.filterClient.name : null;
  let selectedDate: Date = this.filterDate ? new Date(this.filterDate) : null;


  this.loanService.getLoans(pageable, titleGame, nameClient, selectedDate).subscribe(data => {
    this.dataSource.data = data.content;
    this.pageNumber = data.pageable.pageNumber;
    this.pageSize = data.pageable.pageSize;
    this.totalElements = data.totalElements;
  });
}



}
