class Barrage {
	constructor(id, x, y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	draw = function() {
		ctx.drawImage(barrageImg, Number(this.x) - 30, Number(this.y) - 30);
	}
}

class Character {
	constructor(id, x, y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	draw = function(id) {
		if (this.id == id) {
			ctx.drawImage(selfImg, Number(this.x) - 30, Number(this.y) - 30);
			
		} else {
			ctx.drawImage(otherImg, Number(this.x) - 30, Number(this.y) - 30);
		}
	}
}