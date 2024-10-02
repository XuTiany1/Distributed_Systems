# Various TEST sequences encompassing different operations
Tested with the following variations of test cases

```
// customer reservation
addcustomer -> will return customer id
OR addcustomerid,1

addflight,2,266,266
addrooms,toronto,177,177
addcars,toronto,177,177

deleteflight,2
deleterooms,toronto
deletecars,toronto

addcars,mtl,55,55
addflight,1,66,66
addrooms,mtl,77,77

reservecar,1,mtl
reserveflight,1,1
reserveroom,1,mtl
querycars,mtl
queryflight,1
queryrooms,mtl
deletecustomer,1
querycars,mtl

// Specifically to test bundle
addcustomerid,2
addflight,1,166,166
addflight,2,266,266
addrooms,toronto,177,177
addcars,toronto,177,177
bundle,2,1,2,toronto,true,false // car not added
querycustomer,2 -> Should output all the reservaions made for 
										customerid 2 from bundle operation.
querycars,toronto
queryflight,1
queryflight,2
queryrooms,toronto
```
