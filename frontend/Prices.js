document.addEventListener("DOMContentLoaded", () => {

    const ctx = document.getElementById("pricesChart");

    const chart = new Chart(ctx, {
        type: "line",
        data: {
            datasets: [
                {
                label: "Price",
                data: []
                }
            ]
        },
        options: {
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            const p = context.raw;

                            return `${p.ticker}: Price: ${p.y.toFixed(2)}, Time: ${p.label}`;
                        }
                    }
                }
            },
            scales: {
               
                y: {
                 
                    title: {
                        display: true,
                        text: "Price"
                    }
                }
            }
        }
    });
    document.getElementById("prices-graph-form").addEventListener("submit", async (e) => {
    e.preventDefault();

    const ticker = document.getElementById("prices-graph-ticker").value;

    try {
        const res = await fetch(`http://localhost:3010/api/price-data?ticker=${encodeURIComponent(ticker)}`);

        if (!res.ok) throw new Error("Server error");

        const data = await res.json();
        
        const now = new Date();
        const formatDate = (d) =>
            d.toLocaleString("en-GB", {
                day: "2-digit",
                month: "short",
                year: "numeric"
            });

        const baseDate = new Date();
        baseDate.setMonth(baseDate.getMonth() - 120);
        const points = data.points.map(item => {
        const d = new Date(baseDate);
        d.setMonth(d.getMonth() + item.time);

        return {
            x: d,
            y: item.price,
            ticker: ticker.toUpperCase(),
            label: item.price
        };
    });

    chart.data.datasets[0].data = points;
    chart.options.scales.x = {
        type: "time",
        time: {
            unit: "month",
            displayFormats: {
                month: "MM/yy"
            }
        },
    min: points[0].x,
    max: points[points.length - 1].x
    };

      
    chart.update();

    }catch(err){
        document.getElementById("prices-graph-response").innerHTML =
            `<p style="color:red;">Error: ${err.message}</p>`;
    }
    });
    
   
});