function parseCSV(str) {
    return str
        .split(",")
        .map(s => s.trim())
        .filter(Boolean);
}
document.addEventListener("DOMContentLoaded", () => {

    const ctx = document.getElementById("CapitalAllocationLineChart");

    const chart = new Chart(ctx, {
        type: "line",
        data: {
            datasets: [
                {
                label: "CapitalAllocationLine", 
                data: [],
                parsing: false
                }
            ]
        },
        options: {
    scales: {
        x: {
            min: 0,
            type: "linear",
            title: {
               
                display: true,
                text: "Risk (Standard Deviation)"
            }
        },
        y: {
            min: 0,
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
        const res = await fetch(`http://localhost:3010/api/capital-allocation-line?${params}`);

        if (!res.ok) throw new Error("Server error");

        const data = await res.json();
        
        
        const points = data.map(item => {
    return {
        x: item.risk * 100,
        y: item.expectedReturn * 100
    };
});
chart.data.datasets[0].data = points;
    chart.update();

    }catch(err){
        document.getElementById("graph-response").innerHTML =
            `<p style="color:red;">Error: ${err.message}</p>`;
    }
    });
    
   
});