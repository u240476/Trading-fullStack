function parseCSV(str) {
    return str
        .split(",")
        .map(s => s.trim())
        .filter(Boolean);
}
document.addEventListener("DOMContentLoaded", () => {

    const ctx = document.getElementById("EfficientFrontierChart");

    const chart = new Chart(ctx, {
        type: "line",
        data: {
            datasets: [
                {
                label: "Efficient Frontier", data: []
                }
            ]
        },
        options: {
    scales: {
        x: {
      
            type: "linear",
            title: {
               
                display: true,
                text: "Risk (Standard Deviation)"
            }
        },
        y: {
            min: 0,
            max: 10,
            title: {
                display: true,
                text: "Expected Return"
            }
        }
    }
}
    });
    document.getElementById("clear-graph-button")
        .addEventListener("click", () => {

            chart.data.datasets.forEach(d => {
                d.data = [];
            });

            chart.update();
        });

    document.getElementById("graph-portfolio-form").addEventListener("submit", async (e) => {
    e.preventDefault();

    const tickers = document.getElementById("graph-portfolio-tickers-input").value;
    const params = new URLSearchParams({
    tickers: parseCSV(tickers).join(",")
});

    try {
        const res = await fetch(`http://localhost:3010/api/graph-ef-for-standard-deviation?${params}`);

        if (!res.ok) throw new Error("Server error");

        const data = await res.json();
        
        
        const points = data.map(item => {
    return {
        x: item.minPortfolioStandardDeviation * 100,
        y: item.minPortfolioReturn * 100
    };
});
chart.data.datasets[0].data = points;
    document.getElementById("graph-portfolio-tickers-input").value = "";
    chart.update();

    }catch(err){
        document.getElementById("graph-response").innerHTML =
            `<p style="color:red;">Error: ${err.message}</p>`;
    }
    });

    
    
   
});