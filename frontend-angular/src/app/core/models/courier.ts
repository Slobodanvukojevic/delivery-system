export class Courier {
    constructor(
        public id: number,
        public fullName: string,
        public email: string,
        public phone: string,
        public branchId: number,
        public deliveredCount: number,
        public active: boolean
    ) { }
}