const express = require('express');
const axios = require('axios');
const app = express();
const cors = require('cors');
const port = 3000; // 你可以选择其他端口

const { MongoClient,ObjectId } = require('mongodb');

const uri = "mongodb+srv://yli49470:liyifei0201@cluster0.agajmia.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
const client = new MongoClient(uri);
// const client = new MongoClient(uri, { useNewUrlParser: true, useUnifiedTopology: true });


// async function run() {
//   try {
//     const database = client.db('sample_mflix');
//     const movies = database.collection('movies');
//     // Query for a movie that has the title 'Back to the Future'
//     const query = { title: 'Back to the Future' };
//     const movie = await movies.findOne(query);
//     console.log(movie);
//   } finally {
//     // Ensures that the client will close when you finish/error
//     await client.close();
//   }
// }
// run().catch(console.dir);

app.use(express.json());
app.use(cors());

// 允许跨域请求
app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept');
  next();
});

app.get('/api/stock/:symbol', async (req, res) => {
  const symbol = req.params.symbol;
  const API_KEY = 'cnqdmqhr01qkkf7uvo50cnqdmqhr01qkkf7uvo5g'; // 使用API Key
  const url = `https://finnhub.io/api/v1/stock/profile2?symbol=${symbol}&token=${API_KEY}`;

  try {
    const response = await axios.get(url);
    res.json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching stock data');
  }
});

//新加的
app.get('/api/quote/:symbol', async (req, res) => {
  const symbol = req.params.symbol;
  const API_KEY = 'cnqdmqhr01qkkf7uvo50cnqdmqhr01qkkf7uvo5g'; // 使用相同的API Key
  const url = `https://finnhub.io/api/v1/quote?symbol=${symbol}&token=${API_KEY}`;

  try {
    const response = await axios.get(url);
    res.json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching quote data');
  }
});
//
app.get('/api/name/:symbol', async (req, res) => {
  const symbol = req.params.symbol; // 确保股票代码大写
  const API_KEY = 'cnqdmqhr01qkkf7uvo50cnqdmqhr01qkkf7uvo5g';
  const url = `https://finnhub.io/api/v1/search?q=${symbol}&token=${API_KEY}`;

  try {
    const response = await axios.get(url);
    res.json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching stock data');
  }
});

app.get('/api/insider-sentiment/:symbol', async (req, res) => {
  const symbol = req.params.symbol; // 确保股票代码大写
  const API_KEY = 'cnqdmqhr01qkkf7uvo50cnqdmqhr01qkkf7uvo5g';
  const url = `https://finnhub.io/api/v1/stock/insider-sentiment?symbol=${symbol}&from=2022-01-01&token=${API_KEY}`;

  try {
    const response = await axios.get(url);
    res.json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching stock data');
  }
});

app.get('/api/recommendation/:symbol', async (req, res) => {
  const symbol = req.params.symbol; // 确保股票代码大写
  const API_KEY = 'cnqdmqhr01qkkf7uvo50cnqdmqhr01qkkf7uvo5g';
  const url = `https://finnhub.io/api/v1/stock/recommendation?symbol=${symbol}&&token=${API_KEY}`;

  try {
    const response = await axios.get(url);
    res.json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching stock data');
  }
});

app.get('/api/peers/:symbol', async (req, res) => {
  const symbol = req.params.symbol; // 确保股票代码大写
  const API_KEY = 'cnqdmqhr01qkkf7uvo50cnqdmqhr01qkkf7uvo5g';
  const url = `https://finnhub.io/api/v1/stock/peers?symbol=${symbol}&token=${API_KEY}`;

  try {
    const response = await axios.get(url);
    res.json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching stock data');
  }
});

app.get('/api/earnings/:symbol', async (req, res) => {
  const symbol = req.params.symbol; // 确保股票代码大写
  const API_KEY = 'cnqdmqhr01qkkf7uvo50cnqdmqhr01qkkf7uvo5g';
  const url = `https://finnhub.io/api/v1/stock/earnings?symbol=${symbol}&token=${API_KEY}`;

  try {
    const response = await axios.get(url);
    res.json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching stock data');
  }
});

//
app.get('/api/news/:symbol', async (req, res) => {
  const symbol = req.params.symbol; // 股票代码大写
  const API_KEY = 'cnqdmqhr01qkkf7uvo50cnqdmqhr01qkkf7uvo5g'; // 使用您的API密钥
  // 计算日期
  const toDate = new Date();
  const fromDate = new Date();
  fromDate.setDate(toDate.getDate() - 7); // 设置为7天前

  // 格式化日期为 yyyy-mm-dd
  const format = (date) => date.toISOString().split('T')[0];

  const url = `https://finnhub.io/api/v1/company-news?symbol=${symbol}&from=${format(fromDate)}&to=${format(toDate)}&token=${API_KEY}`;

  try {
    const response = await axios.get(url);
    res.json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching stock data');
  }
});







//

app.get('/api/polygon/:symbol', async (req, res) => {
  const symbol = req.params.symbol;
  const API_KEY = 'DM2gnGfU1uOfz8GglJt6uxfN5apWTC4O';

  const toDate = new Date();
  const fromDate = new Date(toDate.getTime() - (7* 24 * 60 * 60 * 1000)); // 当前时间减去24小时

  // 格式化日期为 YYYY-MM-DD
  const to = toDate.toISOString().split('T')[0];
  const from = fromDate.toISOString().split('T')[0];

  
  const url = `https://api.polygon.io/v2/aggs/ticker/${symbol}/range/1/hour/${from}/${to}?apiKey=${API_KEY}`;

  try {
    const response = await axios.get(url);
    res.json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching data from Polygon');
  }
});




app.get('/api/polygon2/:symbol', async (req, res) => {
  const symbol = req.params.symbol;
  const API_KEY = 'DM2gnGfU1uOfz8GglJt6uxfN5apWTC4O';
  // 获取当前日期并格式化为 YYYY-MM-DD
  const currentDate = new Date();
  const to = currentDate.toISOString().split('T')[0];

  // 获取两年前的日期并格式化为 YYYY-MM-DD
  currentDate.setFullYear(currentDate.getFullYear() - 2);
  const from = currentDate.toISOString().split('T')[0];

  
  const url = `https://api.polygon.io/v2/aggs/ticker/${symbol}/range/1/day/${from}/${to}?apiKey=${API_KEY}`;

  try {
    const response = await axios.get(url);
    res.json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching data from Polygon');
  }
});

app.post('/api/favourites', async (req, res) => {
  try {
    await client.connect();
    const database = client.db('HW3');
    const collection = database.collection("favorites");
    
    // const stockInfo = req.body;
    const { stockInfo, quoteInfo } = req.body;
    const document = {
      stockInfo,
      quoteInfo,
    };
    const result = await collection.insertOne(document);
    res.status(201).json({message: `Company added to favourites with id ${result.insertedId}`});
    ;
  } catch (error) {
    console.error(error);
    res.status(500).json({error: "Failed to add company to favourites"});
  } finally {
    await client.close();
  }
});

app.delete('/api/favourites/:ticker', async (req, res) => {
  try {
    await client.connect();
    const database = client.db("HW3");
    const collection = database.collection("favorites");
    // 使用股票代码（ticker）作为标识来删除特定的收藏公司
    const ticker = req.params.ticker;
    // const result = await collection.deleteOne({ ticker: ticker });
    const result = await collection.deleteOne({"stockInfo.ticker": ticker});
    if (result.deletedCount === 1) {
      res.status(200).json({ message: `Company removed from favourites` });
    } else {
      res.status(404).json({ error: "Company not found in favourites" });
    }
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: "Failed to remove company from favourites" });
  } finally {
    await client.close();
  }
});

app.get('/api/favourites', async (req, res) => {
  try {
    await client.connect();
    const database = client.db("HW3");
    const collection = database.collection("favorites");
    const favourites = await collection.find({}).toArray();
    res.status(200).json(favourites);
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: "Failed to fetch favourites" });
  } finally {
    await client.close();
  }
});



app.get('/api/transactions', async (req, res) => {
  try {
    await client.connect();
    const database = client.db("HW3");
    const collection = database.collection("transaction");

    // 获取集合中的所有文档
    const documents = await collection.find({}).toArray();

    res.json(documents);
  } catch (e) {
    console.error(e);
    res.status(500).json({ error: e.message });
  } finally {
    await client.close();
  }
});

app.post('/api/transactions', async (req, res) => {
  try {
    await client.connect();
    const database = client.db('HW3');
    const transactions = database.collection("transaction");
    const balanceDocumentId = "6600c23c2d7f693e34e6407b"; // 余额文档的固定_id

    // 从请求体中解构需要的值，包括用于计算的price
    const { ticker, description, price, quantity } = req.body;
    const totalCost = price * quantity;

    await transactions.updateOne(
      { _id: new ObjectId(balanceDocumentId) },
      { $inc: { balance: -totalCost } }
    );

    // 获取更新后的余额，用于响应
    const updatedBalanceDoc = await transactions.findOne({ _id: balanceDocumentId });
    

    // 检查是否已存在该股票的购买记录
    const existingRecord = await transactions.findOne({ ticker });

    if (existingRecord) {
      // 如果记录存在，基于新的购买更新总成本和数量
      const newTotalCost = existingRecord.totalCost + totalCost;
      const newQuantity = existingRecord.quantity + quantity;
      const newAvgCost = newTotalCost / newQuantity;

      await transactions.updateOne(
        { _id: existingRecord._id },
        {
          $set: {
            quantity: newQuantity,
            totalCost: newTotalCost,
            avgCost: newAvgCost,
            description // 保证description也被更新
          }
        }
      );
      res.json({ message: "Transaction updated successfully" });
    } else {
      // 如果记录不存在，插入一条新记录，不包括price字段
      const avgCost = totalCost / quantity; // 直接计算平均成本

      await transactions.insertOne({
        ticker,
        description,
        quantity,
        totalCost,
        avgCost
      });
      res.json({ message: "Transaction recorded successfully" });
    }
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: "Failed to process the purchase" });
  } finally {
    await client.close();
  }
});




app.patch('/api/transactions', async (req, res) => {
  try {
    await client.connect();
    const database = client.db('HW3');
    const transactions = database.collection("transaction");
    const balanceDocumentId = "6600c23c2d7f693e34e6407b"; // 余额文档的固定_id

    // 从请求体中解构需要的值
    const { ticker, quantity, price } = req.body;
    const decreaseAmount = price * quantity;

    await transactions.updateOne(
      { _id: new ObjectId(balanceDocumentId) },
      { $inc: { balance: +decreaseAmount } }
    );

    // 获取更新后的余额，用于响应
    const updatedBalanceDoc = await transactions.findOne({ _id: balanceDocumentId });

    // 查找指定ticker的记录
    const existingRecord = await transactions.findOne({ ticker });

    if (existingRecord) {
      // 减去新传进来的quantity和totalCost
      const updatedQuantity = existingRecord.quantity - quantity;
      const updatedTotalCost = existingRecord.totalCost - decreaseAmount;

      if (updatedQuantity === 0) {
        // 如果quantity为0，删除这个记录
        await transactions.deleteOne({ ticker });
        res.json({ message: "Transaction deleted successfully" });
      } else if (updatedQuantity > 0) {
        // 更新记录
        const updatedAvgCost = updatedTotalCost / updatedQuantity;
        await transactions.updateOne(
          { ticker },
          {
            $set: {
              quantity: updatedQuantity,
              totalCost: updatedTotalCost,
              avgCost: updatedAvgCost
            }
          }
        );
        res.json({ message: "Transaction updated successfully" });
      } else {
        // 如果更新后的quantity小于0，返回错误
        res.status(400).json({ error: "Invalid quantity: resulting quantity cannot be less than 0." });
      }
    } else {
      // 如果找不到记录，返回错误
      res.status(404).json({ error: "Ticker not found" });
    }
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: "Failed to update the transaction" });
  } finally {
    await client.close();
  }
});





































app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
