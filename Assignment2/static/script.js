let searchData;
let user_stockTicker;

        function handleKeyPress(event) {
        // 检查按下的键是否是 Enter 键的键码是 13
        if (event.key === "Enter" || event.keyCode === 13) {
            // 调用搜索函数
            searchStock();
        }
    }

    

    function searchStock() {

        clearall();

    // 获取用户输入的股票代码
        
        var userInput = document.getElementById('stock_ticker_input').value;
        stockTicker = userInput.toUpperCase();
        user_stockTicker = stockTicker

        if (!user_stockTicker) {
        // 更新错误提示信息
        document.getElementById('error-message-empty').textContent = '! please fill out this field.';
        return;
    }
        document.getElementById('error-message-empty').textContent = '';

        // 通过 Fetch API 将股票代码发送到 Flask 服务器
        fetch('/Assignment2/search?stock_ticker=' + stockTicker)
        .then(response => response.json())
        .then(data => {
            // 将数据保存在变量中
            searchData = data;
            handleSearchResult(data);
        })
}


    function handleSearchResult(data) {
    // 隐藏错误信息和按钮容器
        console.log(searchData);

        document.getElementById('action-buttons').style.display = 'none';

        if (data.company_info != null) {
            // 搜索成功，显示股票信息和按钮
            displayStockInfo(data);

            document.getElementById('action-buttons').style.display = 'block';
            document.getElementById('company-button').focus();
        } else {
            // 搜索失败，显示错误信息
            displayErrorMessage(data.error_message);
        }
}

    function showCompanyInfo(){
        document.getElementById('stock-info-container').style.display = 'block';
        document.getElementById('company-logo').style.display = 'block';
        document.getElementById('stock-summary').style.display = 'none';
        document.getElementById('news-container').style.display = 'none';
        document.getElementById('chart-container').style.display = 'none';
        
    }

    function displayStockInfo(data) {
        var companyLogo = document.getElementById('company-logo');
        companyLogo.src = data['company_info']['logo'];
        companyLogo.alt = 'Company Logo'; // 设置替代文本

        companyLogo.style.display = 'block';

        // 添加每一行的数据
        // 获取相应的HTML元素
        var companyNameElement = document.getElementById('company-name');
        var stockTickerElement = document.getElementById('stock-ticker');
        var exchangeCodeElement = document.getElementById('exchange-code');
        var startDateElement = document.getElementById('start-date');
        var categoryElement = document.getElementById('category');

        // 设置元素的文本内容
        companyNameElement.innerText = data['company_info']['name'];
        stockTickerElement.innerText = data['company_info']['ticker'];
        exchangeCodeElement.innerText = data['company_info']['exchange'];
        startDateElement.innerText = data['company_info']['ipo'];
        categoryElement.innerText = data['company_info']['finnhubIndustry'];
        
        // 显示信息容器
        document.getElementById('stock-info-container').style.display = 'block';

        // 显示按钮和结果
        document.getElementById('action-buttons').style.display = 'block';
    }
    


        function deleteText() {
            document.getElementById('stock_ticker_input').value = ''; // 清空输入框内容
            clearall();
        }


       

        function clearall() {
            // 在这里添加清除搜索结果的逻辑，例如隐藏元素、重置内容等
            // 例如：
            document.getElementById('company-logo').style.display = 'none';
            document.getElementById('stock-info-container').style.display = 'none';
            document.getElementById('action-buttons').style.display = 'none';
            document.getElementById('error-message-no-result').style.display = 'none';
            document.getElementById('stock-summary').style.display = 'none';
            document.getElementById('news-container').style.display = 'none';
            document.getElementById('chart-container').style.display = 'none';
            
            
            
            // 其他清除逻辑...
}
    

        function displayErrorMessage(error_message) {
            // 获取错误消息的元素
            var errorMessageElement = document.getElementById('error-message-no-result');

            // 设置错误消息的内容
            errorMessageElement.textContent = error_message;

            // 显示错误消息
            errorMessageElement.style.display = 'block';
        }
        function showStockSummary(){
            const epochTime = searchData['quote']['t'];
            var date = new Date(epochTime * 1000);

            // 月份的英文名称数组
            var monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
            ];

            // 获取日期的日、月、年
            var day = date.getDate();
            var monthIndex = date.getMonth();
            var year = date.getFullYear();

            // 格式化日期为 "13 February 2024" 的形式
            var formattedDate = day + ' ' + monthNames[monthIndex] + ', ' + year;

            // 将格式化后的日期设置到元素中
            document.getElementById('Trading-Day').innerText = formattedDate;


            document.getElementById('StockTicker-Symbol').innerText=searchData['company_info']['ticker'];
            document.getElementById('Previous-Closing-Price').innerText=searchData['quote']['pc']
            document.getElementById('Opening-Price').innerText=searchData['quote']['o']
            document.getElementById('High-Price').innerText=searchData['quote']['h']
            document.getElementById('Low-Price').innerText=searchData['quote']['l']

            
            var changeElement = document.getElementById('Change');
            var changePercentElement = document.getElementById('Change-Percent');

            // 获取相应的数据
            var changeValue = searchData['quote']['d'];
            var changePercentValue = searchData['quote']['dp'];
            // 设置元素的文本内容
            changeElement.innerText = changeValue;
            changePercentElement.innerText = changePercentValue;
            if (changeValue < 0) {
            // 如果 Change 是负数，添加红色箭头
                changeElement.innerHTML += ' <img src="static/image/RedArrowDown.png" alt="Red Arrow" style="width: 15px; height: 15px">';
            } else if (changeValue > 0) {
                // 如果 Change 是正数，添加绿色箭头
                changeElement.innerHTML += ' <img src="static/image/GreenArrowUp.png" alt="Green Arrow" style="width: 15px; height: 15px">';
            }   

            if (changePercentValue < 0) {
            // 如果 Change 是负数，添加红色箭头
                changePercentElement.innerHTML += ' <img src="static/image/RedArrowDown.png" alt="Red Arrow" style="width: 15px; height: 15px">';
            } else if (changePercentValue > 0) {
                // 如果 Change 是正数，添加绿色箭头
                changePercentElement.innerHTML += ' <img src="static/image/GreenArrowUp.png" alt="Green Arrow" style="width: 15px; height: 15px">';
            }
            
            document.getElementById('text2').innerText = searchData['recommendation'][0]['strongSell'];
            document.getElementById('text3').innerText = searchData['recommendation'][0]['sell'];
            document.getElementById('text4').innerText = searchData['recommendation'][0]['hold'];
            document.getElementById('text5').innerText = searchData['recommendation'][0]['buy'];
            document.getElementById('text6').innerText = searchData['recommendation'][0]['strongBuy'];



            document.getElementById('stock-summary').style.display = 'block';
            document.getElementById('company-logo').style.display = 'none';
            document.getElementById('stock-info-container').style.display = 'none';
            document.getElementById('news-container').style.display = 'none';
            document.getElementById('chart-container').style.display = 'none';
            
            

        }

        function showLatestNews(){
            var newsList = searchData['news'];

            // Initialize an empty array to store the extracted data
            var extractedData = [];

            // Initialize a counter to keep track of the number of extracted items
            var count = 0;

            // Iterate through each news object using a while loop
            var index = 0;
            while (count < 5 && index < newsList.length) {
                var newsItem = newsList[index];

                // Check if 'image', 'url', 'headline', and 'datetime' keys are present and not empty
                if (newsItem['image'] && newsItem['url'] && newsItem['headline'] && newsItem['datetime']) {
                    // Extract the required data and push it to the array
                    extractedData.push({
                        'image': newsItem['image'],
                        'url': newsItem['url'],
                        'headline': newsItem['headline'],
                        'datetime': newsItem['datetime'],
                    });
                    count++;
                }

                index++; // Move to the next news item
            }


            var monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
            ];


            for (var i = 0; i < extractedData.length; i++) {
                var newsItem = extractedData[i];
                var date = new Date(newsItem['datetime'] * 1000);
                var day = date.getDate();
                var monthIndex = date.getMonth();
                var year = date.getFullYear();

            // 格式化日期为 "13 February 2024" 的形式
                var formattedDate = day + ' ' + monthNames[monthIndex] + ', ' + year;


                var newsRectangleId = 'news-rectangle' + (i + 1); // Generate the corresponding news rectangle ID

                // Get the news rectangle element
                var newsRectangle = document.getElementById(newsRectangleId);

                // Create HTML content for the news item
                var content = '<div class="news-item">' +
                    '<div class="news-image">' +
                    '<img src="' + newsItem['image'] + '" alt="News Image" style="width: 130px; height: 100px;float:left;">' +
                    '</div>' +
                    '<div class="news-info" style="text-align: left; margin-left: 150px;">' +
                    '<p><strong>' + newsItem['headline'] + '</strong></p>' +
                    '<p >' + formattedDate + '</p>' +
                    '<a href="' + newsItem['url'] + '" class="news-link" target="_blank">See Original Post</a>' +                    '</div>' +
                    '</div>';

                // Set the HTML content to the news rectangle
                newsRectangle.innerHTML = content;
}

            document.getElementById('stock-summary').style.display = 'none';
            document.getElementById('company-logo').style.display = 'none';
            document.getElementById('stock-info-container').style.display = 'none';
            document.getElementById('chart-container').style.display = 'none';
            document.getElementById('news-container').style.display = 'block';
            
        }


        function showCharts(){
            
            var t = [];
            var c = [];
            var v = [];

            var dataPoints = [];
            var dataPoints2 = [];

            var len = searchData['chart']['count'];
            for (var i = 0; i < len; i++) {
                var result = searchData['chart']['results'][i];
/*
                var date = new Date(result['t']);
                
                var month = date.getMonth() + 1; // 实际月份
                var day = date.getDate();
                var formattedDate = month + '-' + day; 
                        
                var options = { day: 'numeric', month: 'short' };
                var formattedDate = date.toLocaleDateString('en-US', options);
                */
                
                // 将每个 t、c、v 存储到相应的数组中
                
                t.push(result['t']);
                c.push(result['c']);
                v.push(result['v']);

                var dataPoint = { x: result['t'], y: result['c'] };
                dataPoints.push(dataPoint);

                var dataPoint2 = { x: result['t'], y: result['v'] };
                dataPoints2.push(dataPoint2);
                
            }
            console.log(dataPoints);
            createChart(dataPoints,dataPoints2);  

            document.getElementById('stock-summary').style.display = 'none';
            document.getElementById('company-logo').style.display = 'none';
            document.getElementById('stock-info-container').style.display = 'none';
            document.getElementById('news-container').style.display = 'none';
            document.getElementById('chart-container').style.display = 'block';
            
        }

        function createChart(dataPoints, dataPoints2) {
           
            var seriesData = dataPoints.map(point => {
                return {
                    x: point.x,
                    y: point.y
                };
            });

            var seriesData2 = dataPoints2.map(point => {
                return {
                    x: point.x,
                    y: point.y
                };
             });

             var minYValue = Math.min(
                ...seriesData.map(point => point.y)
            )
            var maxYValue = Math.max(
                ...seriesData2.map(point => point.y)
            )


            var today = new Date();
            var formattedToday = today.toISOString().split('T')[0]; // Get ISO date and extract yyyy-mm-dd

            Highcharts.stockChart('chart-container', {
                title: {
                    text:  'Stock Price ' + user_stockTicker + ' ' +formattedToday
                },
                subtitle: {
                    text: '<a href="https://polygon.io/" target="_blank">Source: Polygon.io</a>', // 使用 HTML 标签创建链接
                    useHTML: true // 允许使用 HTML
                },
                xAxis: {
                    type: 'datetime',
                    labels: {
                        step: 1,
                        formatter: function () {
                            return Highcharts.dateFormat('%e %b', new Date(this.value));
                        }
                    },
                    gapGridLineWidth: 1
              },
                yAxis: [ { // Secondary yAxis (right side)
                    title: {
                        text: 'Stock Price',
                        style: {
                            color: 'black'
                        }
                    },
                    labels: {
                        format: '{value}',
                        style: {
                            color: 'black'
                        }
                    },
                    opposite: false,
                    min: minYValue,
                    tickAmount: 6,

                },
                { 
                    title: {
                        text: 'Volume',
                        style: {
                            color: 'black'
                        }
                    },
                    labels: {
                        format: '{value}',
                        style: {
                            color: 'black'
                        },
                        formatter: function () {
                            // 将y轴标签值除以1000000，并添加'M'单位
                            return (this.value / 1000000) + 'M';
                        }
                        
                    },
                    max:maxYValue*2.5,
                    tickAmount: 6,
                }],
                events: {
                    load: function () {
                        // 在图表加载后，使用 setExtremes 方法动态设置最小值
                        this.yAxis[0].setExtremes(minYValue);
                    }
                 },





                rangeSelector: {
                    buttons: [{
                        type: 'day',
                        count: 7,
                        text: '7d'
                    }, 
                    {
                        type: 'day',
                        count: 15,
                        text: '15d'
                    }, {
                        type: 'month',
                        count: 1,
                        text: '1m'
                    }, {
                        type: 'month',
                        count: 3,
                        text: '3m'
                    }, {
                        type: 'month',
                        count: 6,
                        text: '6m'
                    }],
                    selected: 0,
                    inputEnabled: false
                },
                series: [{
                    name: 'Stock price',
                    type: 'area',
                    data: seriesData,
                    yAxis: 0, // Link to the first yAxis (left side)
                    fillColor: {
                        linearGradient: {
                            x1: 0,
                            y1: 0,
                            x2: 0,
                            y2: 1
                        },
                        stops: [
                            [0, Highcharts.getOptions().colors[0]],
                            [1, Highcharts.color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                        ]
                    },
                    //color: Highcharts.getOptions().colors[0],
                    tooltip: {
                        valueDecimals: 2
                    }
                }, {
                    name: 'Volume',
                    type: 'column',
                    data: seriesData2,
                    yAxis: 1, // Link to the second yAxis (right side)
                    color: 'black',
                    pointWidth: 5,
                    tooltip: {
                        valueDecimals: 2
                    }
                }]
            });
}  