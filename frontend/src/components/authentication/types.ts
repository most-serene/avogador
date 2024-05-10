export interface User {
  id: string;
  email: string;
  givenName?: string;
  familyName?: string;
  isProfessor: boolean;
  isSuperuser: boolean;
}
