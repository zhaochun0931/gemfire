start locator --name=l1

start server --name=s1 --server-port=0 --enable-time-statistics=1 --statistic-archive-file=/tmp/s1.gfs
start server --name=s2 --server-port=0 --enable-time-statistics=1 --statistic-archive-file=/tmp/s2.gfs
start server --name=s3 --server-port=0 --enable-time-statistics=1 --statistic-archive-file=/tmp/s3.gfs

create region --name=exampleRegion --type=PARTITION_PERSISTENT --redundant-copies=1 --total-num-buckets=4

put --key=1  --value=hello1  --region=/exampleRegion
put --key=2  --value=hello2  --region=/exampleRegion
put --key=3  --value=hello3  --region=/exampleRegion
put --key=4  --value=hello4  --region=/exampleRegion
put --key=5  --value=hello5  --region=/exampleRegion
put --key=6  --value=hello6  --region=/exampleRegion
put --key=7  --value=hello7  --region=/exampleRegion
put --key=8  --value=hello8  --region=/exampleRegion
put --key=9  --value=hello9  --region=/exampleRegion
put --key=10 --value=hello10 --region=/exampleRegion
