export class Order {
    constructor(
        public id: number,
        public senderName: string,
        public senderPhone: string,
        public customerName: string,
        public customerPhone: string,
        public weight: number,
        public pickupAddress: string,
        public dropoffAddress: string,
        public status: string,
        public deliveryMethod: string,
        public pickupCode: string | null,
        public selectedBranchId: number | null,
        public selectedLockerId: number | null,
        public price: number,
        public createdAt: string
    ) { }
}