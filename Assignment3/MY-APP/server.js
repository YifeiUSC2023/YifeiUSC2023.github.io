const express = require('express')
const cors = require('cors')
const app = express()
const path = require('path')
app.use(cors({
    origin: '*'
}))

app.use(express.static(path.join(__dirname, 'dist/my-app/browser')));

app.get('/*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist/my-app/browser/index.html'))
})

const port = process.env.PORT || 8080
app.listen(port, () => {
    console.log(`Server is running on port ${port}`)
})