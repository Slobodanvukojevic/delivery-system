export class Branch {
    constructor(
        public id: number,
        public name: string,
        public address: string,
        public latitude: number,
        public longitude: number,
        public workingHours: string,
        public phone: string,
        public active: boolean
    ) { }
}