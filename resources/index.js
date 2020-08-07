

function sendJSON(){

    //Testin this, send userlist request via post
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "", true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({
        type: 5,
        sender: document.getElementById("uname").value

    }));

}
