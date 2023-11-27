import{ Loan } from "./Loan";

export const LOAN_DATA: Loan[]=[
  {id:1 , game: {id: 2, title: 'Juego 1', age: 6, category: { id: 1, name: 'Categoría 1' }, author: { id: 1, name: 'Autor 1', nationality: 'Nacionalidad 1' }}, client:{ id:1, name: 'Juan'}, startDate: new Date('2023-11-16'), endDate: new Date('2023-11-30')},
  { id: 2, game: { id: 4, title: 'Juego 2', age: 8, category: { id: 2, name: 'Categoría 2' }, author: { id: 2, name: 'Autor 2', nationality: 'Nacionalidad 2' } }, client: { id: 2, name: 'Maria' }, startDate: new Date('2023-12-01'), endDate: new Date('2023-12-15') },
  { id: 3, game: { id: 3, title: 'Juego 3', age: 10, category: { id: 3, name: 'Categoría 3' }, author: { id: 3, name: 'Autor 3', nationality: 'Nacionalidad 3' } }, client: { id: 3, name: 'Pedro' }, startDate: new Date('2023-12-10'), endDate: new Date('2023-12-20') }
]
