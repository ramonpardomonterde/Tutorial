import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { LoanService } from '../loan.service';
import { Loan } from '../model/Loan';
import { ClientService } from 'src/app/client/client.service';
import { GameService } from 'src/app/game/game.service';
import { Client } from 'src/app/client/model/Client';
import { Game } from 'src/app/game/model/Game';
import { Author } from 'src/app/author/model/Author';
import { Category } from 'src/app/category/model/Category';

@Component({
  selector: 'app-loan-edit',
  templateUrl: './loan-edit.component.html',
  styleUrls: ['./loan-edit.component.scss']
})
export class LoanEditComponent implements OnInit {

  loan: Loan;
  clients: Client[];
  games: Game[];
  authors: Author[];
  categories: Category[];
  errorSavingLoan: string;

  constructor(
    public dialogRef: MatDialogRef<LoanEditComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private loanService: LoanService,
    private clientService: ClientService,
    private gameService: GameService
  ) {}

  ngOnInit(): void {
    if (this.data.loan != null) {
      this.loan = Object.assign({}, this.data.loan);
    } else {
      this.loan = new Loan();
    }
    
    // Cargar clientes
    this.clientService.getClients().subscribe(
      clients => {
        this.clients = clients;

        if (this.loan.client != null) {
          let client = this.clients.find(c => c.id === this.loan.client.id);
          if (client != null) {
            this.loan.client = client;
          }
        }
      },
    );

    
    // Cargar juegos
    this.gameService.getGames().subscribe(
      games => {
        this.games = games;

        if (this.loan.game != null) {
          let game = this.games.find(g => g.id === this.loan.game.id);
          if (game != null) {
            this.loan.game = game;
          }
        }
      },
    );
  }

  onSave() {
    this.loanService.saveLoan(this.loan).subscribe(
      result => {
        this.dialogRef.close();
      },
      error => {
        if( error === "La fecha de fin no puede ser anterior a la fecha de inicio"){
          this.errorSavingLoan = "Error al guardar el préstamo. La fecha de fin no puede ser anterior a la fecha de inicio.";
        } else if(error=== "Plazo de tiempo mayor a 14 dias" ){
          this.errorSavingLoan = "Error al guardar el préstamo. Plazo de tiempo mayor a 14 dias";
        } else if(error === "El mismo juego no puede estar prestado a dos clientes distintos en un mismo día"){
          this.errorSavingLoan = "Error al guardar el prestamo. El mismo juego no puede estar prestado a dos clientes distintos en un mismo día";
        }else if( error === "Un mismo cliente no puede tener prestados más de 2 juegos en un mismo día"){
          this.errorSavingLoan ="Error al guardar el prestamo. Un mismo cliente no puede tener prestados más de 2 juegos en un mismo día";
        }
      }
    );
  }

  onClose() {
    this.dialogRef.close();
  }
}
