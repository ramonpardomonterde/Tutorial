import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { Pageable } from '../core/model/page/Pageable';
import { Loan } from './model/Loan';
import { LoanPage } from './model/LoanPage';
import { catchError } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';



@Injectable({
  providedIn: 'root'
})
export class LoanService {

  constructor(
    private http: HttpClient
  ) { }

  
  getLoans(pageable: Pageable, gameTitle?: string, clientName?: string, date?: Date): Observable<LoanPage> {
    let params = '';

    if (gameTitle != null) {
      params += 'gameTitle='+gameTitle + '&';
    }

    if (clientName != null) {
      params += 'clientName='+clientName  + '&';
    }

    if (date != null) {
      params += 'date=' + date.toISOString();
    }

    let url = 'http://localhost:8080/loan'

    if (params === '') {
      
      return this.http.post<LoanPage>(url, { pageable: pageable });
    } else {
      url += '?' + params;

      return this.http.post<LoanPage>(url, { pageable: pageable });
    }
  }

  saveLoan(loan: Loan): Observable<void> {
    let url = 'http://localhost:8080/loan';
        if (loan.id != null) url += '/'+loan.id;
        return this.http.put<void>(url, loan).pipe(
          catchError((error: HttpErrorResponse) => {
            if (error.status === 400) {
              return throwError(error.error);
            } 
            return throwError('Error al guardar el préstamo.' + error.error);
          })
        );
      
        
  }

  deleteLoan(idLoan : number): Observable<void> {
    return this.http.delete<void>('http://localhost:8080/loan/'+idLoan);

  }    
}
