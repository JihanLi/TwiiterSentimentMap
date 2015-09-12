# TwiiterSentimentMap
Generally, This web app reads a stream of tweets from the Twitter Live API, calls sentiment API to calculate the average sentiment  of tweets in near real time, and shows the tweets location and average sentiment on Google Map.

There are two modules here. The first module Server is responsible for reading data, pushing data to AWS SQS queue, and update UI using webscoket. The second model Worker is another instance running on AWS EC2, it installs a daemon on EC2 instance in the Auto Scaling group to process Amazon SQS messages in the worker environment tier. In the process, worker calculates each tweet's sentiment calling Alchemy Sentiment API. After than, it sends notification to Server, so Server can start updating UI.
