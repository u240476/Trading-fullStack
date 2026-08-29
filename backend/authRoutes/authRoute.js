const express = require("express");
const router = express.Router();

const authMiddleware = require("../middleware/authMiddleware");
const authController = require("../authControllers/authController");

const authLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, 
    limit: 10,                 
    standardHeaders: "draft-7",
    legacyHeaders: false,
    message: {
        error: "Too many requests. Please try again later."
    }
});

router.post("/register", authController.register);
router.post("/login", authController.login);
router.delete("/", authMiddleware, authController.deleteAccount);

module.exports = router;    