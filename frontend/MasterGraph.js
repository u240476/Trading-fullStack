document.addEventListener("DOMContentLoaded", () => {

    const ctx = document.getElementById("MasterChart");

    const chart = new Chart(ctx, {
        type: "scatter",
        data: {
            datasets: [
{
    label: "Efficient Frontier",
    data: [],
    showLine: true,
    borderColor: "#00d4ff",
    borderWidth: 4,
    pointRadius: 0,
    tension: 0.15,
    order: 0
},
{
    label: "Capital Allocation Line",
    data: [],
    showLine: true,
    borderColor: "#8b949e",
    borderDash: [6, 6],
    borderWidth: 2,
    pointRadius: 0,
    order: 1
},
{
    label: "Minimum Variance Portfolio",
    data: [],
    pointBackgroundColor: "#00e676",
    pointBorderColor: "#0d1117",
    pointBorderWidth: 3,
    pointRadius: 7,
    pointHoverRadius: 10,
    order: 10
},
{
    label: "Tangency Portfolio",
    data: [],
    pointBackgroundColor: "#ffb300",
    pointBorderColor: "#0d1117",
    pointBorderWidth: 3,
    pointRadius: 8,
    pointHoverRadius: 11,
    order: 11
}
]
        },
        options: {
            scales: {
                x: {
                    title: { display: true, text: "Standard Deviation (%)" }
                },
                y: {
                    title: { display: true, text: "Expected Return (%)" }
                }
            }
        }
    });

    function parseCSV(input) {
        return input
            .split(/[\s,]+/)
            .map(x => x.trim())
            .filter(Boolean);
    }

    function setDataset(index, data) {
        chart.data.datasets[index].data = data;
    }

    document.getElementById("clear-graph-button")
        .addEventListener("click", () => {

            chart.data.datasets.forEach(d => {
                d.data = [];
            });

            chart.update();
        });

    function renderMasterGraph(data, tickers) {

        setDataset(0, data.efData.map(p => ({
            x: p.minPortfolioStandardDeviation  * 100,
            y: p.minPortfolioReturn  * 100
        })));

        setDataset(1, data.calData.map(p => ({
            x: p.risk  * 100,
            y: p.expectedReturn * 100
        })));

        setDataset(2, [{
            x: data.mvpData.portfolioStandardDeviation * 100,
            y: data.mvpData.portfolioReturn * 100,
            ticker: "MVP"
        }]);

        setDataset(3, [{
            x: data.tpData.portfolioStandardDeviation * 100,
            y: data.tpData.portfolioReturn * 100,
            ticker: "TP"
        }]);

        chart.update();
    }

    document.getElementById("master-graph-form")
        .addEventListener("submit", async (e) => {

            e.preventDefault();

            const input = document.getElementById("master-graph-tickers-input").value;

            if (!input.trim()) {
                alert("Please enter tickers");
                return;
            }

            const tickers = parseCSV(input);

            try {
                const params = new URLSearchParams({
                    tickers: tickers.join(",")
                });

                const res = await fetch(
                    `http://localhost:3010/api/master-graph?${params.toString()}`
                );

                if (!res.ok) {
                    throw new Error(await res.text());
                }

                const data = await res.json();
                document.getElementById("master-graph-tickers-input").value = "";
                renderMasterGraph(data, tickers);

            } catch (err) {
                console.error("Master graph error:", err);
            }
        });
});