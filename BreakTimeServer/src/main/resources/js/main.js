var countInterval;
var notice;
var number;

var objectMap;
var functionMap;

var canvas;
var ctx;

var barrageImg;
var selfImg;
var otherImg;
var backImg;

var webSocket;

var characterId;

document.addEventListener('DOMContentLoaded', function() {
	initializeVariables();
	initializeControlEvent();
	initializeDefaultServerIP();
});

function initializeVariables() {
	objectMap = new Map();
	functionMap = new Map();
	functionMap.set('B', handleBarrage);
	functionMap.set('C', handleCharacter);
	functionMap.set('I', handleId);
	functionMap.set('S', handleStart);
	functionMap.set('O', handleOn);
	functionMap.set('E', handleError);
	functionMap.set('R', handleResult);
	
	canvas = document.getElementById('stage');
	ctx = canvas.getContext('2d');
	
	barrageImg= new Image();
	barrageImg.src = './img/raindrop.png';

	selfImg= new Image();
	selfImg.src = './img/character_self.png';

	otherImg= new Image();
	otherImg.src = './img/character_other.png';

	backImg= new Image();
	backImg.src = './img/background.png';
	
	notice = document.getElementById('notice');
	number = 3;
}

function initializeWebSocket() {
	if (webSocket != null) {
		webSocket.close();
	}

	webSocket = new WebSocket(document.getElementById('ip').value);

	webSocket.onopen = function() {
		console.log('webSocket open');
		waitReadyState();
	}

	webSocket.onclose = function() {
		console.log('webSocket close');
	}

	webSocket.onmessage = handleMessage;
}

function handleMessage(e) {
	//console.log(Date.now());
	
	ctx.clearRect(0, 0, canvas.width, canvas.height);
	ctx.drawImage(backImg, 0, 0);

	var dataList = e.data.split(':');
	dataList
		.map(splitData)
		.forEach(callFunction);
}

function splitData(data) {
	return data.split(',');
}

function callFunction(dataArray) {
	var func = functionMap.get(dataArray[0]);
	func(dataArray);
}

function handleBarrage(dataArray) {
	var barrage = new Barrage(dataArray[1], dataArray[2], dataArray[3]);
	barrage.draw();
}

function handleCharacter(dataArray) {
	var character = new Character(dataArray[1], dataArray[2], dataArray[3]);
	character.draw(characterId);
}

function handleId(dataArray) {
	characterId = dataArray[1];
	console.log('character id: ' + characterId);
}

function handleStart() {
	countInterval = setInterval(showCount, 1000);
	showCount();
}

function showCount() {
	notice.style['font-size'] = '';

	if (number == 0) {
		notice.innerText = '';
		number = 3;
		clearInterval(countInterval);
		return;
	}
	
	notice.innerText = number;
	number--;
}

function handleOn() {
	notice.style['font-size'] = '20px';
	notice.innerText = '게임 진행 중';
}

function handleError() {
	notice.style['font-size'] = '20px';
	notice.innerText = '플레이어가 모두 접속하지 않아 게임을 시작할 수 없습니다.';
}

function handleResult(dataArray) {
	var result = Number(dataArray[1]) / 1000;
	
	notice.style['font-size'] = '20px';
	notice.innerText = '기록: ' + result + ' 초 ' ;
}

function initializeControlEvent() {
	document.addEventListener('keydown', function(e) {
		if (e.code == 'ArrowRight') {
			webSocket.send('RON');
		}
		else if (e.code == 'ArrowLeft') {
			webSocket.send('LON');
		}
		else if (e.code == 'Space') {
			notice.innerText = '';
			initializeWebSocket()
		}
		else if (e.code == 'Enter') {
			webSocket.send('START');
		}
	});
	
	document.addEventListener('keyup', function(e) {
		if (e.code == 'ArrowRight') {
			webSocket.send('ROFF');
		}
		else if (e.code == 'ArrowLeft') {
			webSocket.send('LOFF');
		}
	});
}

function initializeDefaultServerIP() {
	document.getElementById('ip').value = 'ws://10.10.20.116:45000/break';
}

async function waitReadyState() {
	var opened = await connection(webSocket);
	if (opened) {
	  webSocket.send('REQUEST_ID');
	}
}

async function connection (socket, timeout = 10000) {
  const isOpened = () => (socket.readyState === WebSocket.OPEN)

  if (socket.readyState !== WebSocket.CONNECTING) {
    return isOpened()
  }
  else {
    const intrasleep = 100
    const ttl = timeout / intrasleep // time to loop
    let loop = 0
    while (socket.readyState === WebSocket.CONNECTING && loop < ttl) {
      await new Promise(resolve => setTimeout(resolve, intrasleep))
      loop++
    }
    return isOpened()
  }
}