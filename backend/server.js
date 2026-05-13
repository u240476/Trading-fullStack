const express = require("express");
const cors = require("cors");

const app = express();

app.use(cors());
app.use(express.json());

const expectedReturnsRoute = require("./routes/ExpectedReturnsRoute");
const expectedPortfolioReturnsRoute = require("./routes/ExpectedPortfolioReturnsRoute");
const mvpWeightsRoute = require("./routes/MVPWeightsReturnsRoute");
const varianceRoute = require("./routes/VarianceRoute");
const standardDeviationRoute = require("./routes/StandardDeviationRoute");
const portfolioStandardDeviationRoute = require("./routes/PortfolioStandardDeviationRoute");
const portfolioVarianceRoute = require("./routes/PortfolioVarianceRoute");

app.use("/api/expected-returns", expectedReturnsRoute);
app.use("/api/expected-portfolio-returns", expectedPortfolioReturnsRoute);
app.use("/api/mvp", mvpWeightsRoute);
app.use("/api/variance", varianceRoute);
app.use("/api/standard-deviation", standardDeviationRoute);
app.use("/api/portfolio-standard-deviation", portfolioStandardDeviationRoute);
app.use("/api/portfolio-variance", portfolioVarianceRoute);


app.get("/", (req, res) => {
    res.send("Server is running");
});

app.listen(3010, () => {
    console.log("server is running on http://localhost:3010");
});