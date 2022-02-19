class Barrage {
	constructor(dataArray) {
		this.id = dataArray[1];
		this.x = dataArray[2];
		this.y = dataArray[3];
	}
	
	draw = function() {
		ctx.drawImage(barrageCanvas, Number(this.x) - 30, Number(this.y) - 30);
	}
	
	update = function(dataArray) {
		this.x = dataArray[2];
		this.y = dataArray[3];
	}
}

class Character {
	constructor(dataArray) {
		this.id = dataArray[1];
		this.x = dataArray[2];
		this.y = dataArray[3];
	}
	
	draw = function() {
		if (this.id = characterId) {
			ctx.drawImage(selfCanvas, Number(this.x) - 30, Number(this.y) - 30);
	
		} else {
			ctx.drawImage(otherCanvas, Number(this.x) - 30, Number(this.y) - 30);
		}
	}
	
	update = function(dataArray) {
		this.x = dataArray[2];
		this.y = dataArray[3];
	}
}