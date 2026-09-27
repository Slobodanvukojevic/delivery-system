export class User {
    constructor(
        public id: number,
        public email: string,
        public fullName: string,
        public role: string,
        public token: string
    ) { }
}