import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError  } from 'rxjs';
import { Client } from './model/Client'
import { CLIENT_DATA } from './model/mock-clients';
import { catchError } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class ClientService {

  constructor(
    private http: HttpClient
  ) { }

  getClients(): Observable<Client[]>{
    return this.http.get<Client[]>('http://localhost:8080/client');
  }
  saveClient( client: Client ): Observable<Client>{
    let url = 'http://localhost:8080/client';
        if (client.id != null) url += '/'+client.id;

        return this.http.put<Client>(url, client)
  .pipe(
    catchError(error => {
      if (error.status === 400) { 
        
        return throwError('Error al guardar el cliente. Nombre duplicado.');
      }
      return of(null); 
    })
  );
  }

  deleteCategory(idClient: number ): Observable<any> {
    return this.http.delete('http://localhost:8080/client/'+idClient);
  }
}
