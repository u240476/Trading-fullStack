const express = require("express");
const cors = require("cors");

const app = express();

app.use(cors());
app.use(express.json());



const expectedReturnsRoute = require("./routes/ExpectedReturnsRoute");
const expectedPortfolioReturnsRoute = require("./routes/ExpectedPortfolioReturnsRoute");
const mvpWeightsRoute = require("./routes/MVPWeightsReturnsRoute");
const tpWeightsRoute = require("./routes/TPWeightsReturnsRoute");
const varianceRoute = require("./routes/VarianceRoute");
const standardDeviationRoute = require("./routes/StandardDeviationRoute");
const portfolioStandardDeviationRoute = require("./routes/PortfolioStandardDeviationRoute");
const portfolioVarianceRoute = require("./routes/PortfolioVarianceRoute");
const graphDataRoute = require("./routes/GraphDataRoute");
const graphMVPDataRoute = require("./routes/GraphMVPDataRoute");
const graphTPDataRoute = require("./routes/GraphTPDataRoute");
const graphPortfolioDataRoute = require("./routes/GraphPortfolioDataRoute");
const priceReturnRoute = require("./routes/PriceReturnRoute");
const graphEfficientFrontierForSTDVRoute = require("./routes/GraphEfficientFrontierForGivenSTDVRoute");
const graphCalRoute = require("./routes/GraphCalRoute");

app.use("/api/expected-returns", expectedReturnsRoute);
app.use("/api/expected-portfolio-returns", expectedPortfolioReturnsRoute);
app.use("/api/mvp", mvpWeightsRoute);
app.use("/api/tp", tpWeightsRoute);
app.use("/api/variance", varianceRoute);
app.use("/api/standard-deviation", standardDeviationRoute);
app.use("/api/portfolio-standard-deviation", portfolioStandardDeviationRoute);
app.use("/api/portfolio-variance", portfolioVarianceRoute);
app.use("/api/graph-data", graphDataRoute);
app.use("/api/graph-mvp-data", graphMVPDataRoute);
app.use("/api/graph-tp-data", graphTPDataRoute);
app.use("/api/graph-portfolio-data", graphPortfolioDataRoute);
app.use("/api/price-data", priceReturnRoute);
app.use("/api/graph-ef-for-standard-deviation", graphEfficientFrontierForSTDVRoute);
app.use("/api/capital-allocation-line", graphCalRoute);

app.get("/", (req, res) => {
    res.send("Server is running");
});

app.listen(3010, () => {
    console.log("server is running on http://localhost:3010");
});