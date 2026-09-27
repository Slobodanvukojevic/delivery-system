export class Locker {
    constructor(
        public id: number,
        public locationName: string,
        public address: string,
        public latitude: number,
        public longitude: number,
        public totalCompartments: number,
        public availableCompartments: number,
        public active: boolean
    ) { }
}