async function draw() {

    let pts = await fetch('./pts.json').then( response => response.json() )
    let canvas = document.getElementById('canvas');
    let ctx = canvas.getContext('2d');

    pts = pts.map( pt => [ 5 + pt[0] * 0.9 * canvas.height, 5 + pt[1] * 0.9 * canvas.height ] );
    ctx.beginPath();
    pts.map( pt => ctx.lineTo( pt[0] , pt[1] ) );
    ctx.stroke();

}

window.addEventListener('load', draw());
