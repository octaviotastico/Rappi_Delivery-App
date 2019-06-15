# Rapy

In this project we built an API for an app like Rappi/Glovo/PedidosYa in [Scala]. The main idea is that you have two type of users: consumers and providers.
Consumers make orders from the set of available items, and the providers must deliver the orders to the consumers.  

---
### Project structure ideas:

#### class Location `extends from Model`
- name: string
- coordX: int
- coordY: int

#### class Item `extends from Model`
- name : String
- description: String
- price: String
- provider_id: Int

#### class Order `extends from Model`
- consumer: Int
- provider: Int
- store: Int
- total: Int
- products_id: list[Int]
- products_amounts: list[Int]
- state: String

#### class User `extends from Model`
- username: String
- location: Int
- category: String
- storeName: Option[String]
- maxDeliveryDistance: Option[Int]
- protected balance: Float

---
### Implementation ideas

All our classes **inherit** from the trait **Model**, to be attached to a **unique** id. This is very convenient because every item on a database has to be referable with an id.  Every corresponding object also **inherits** from **ModelCompanion**, another trait, to be able to use all the helper functions like filter, exists and find. We use all those functions on the API to obtain information to decide what to do with the incoming input. We can use those functions with any type which extends from **Model**, so we save a lot of code with this **polymorphism**.

To deal with the representation of Consumers and Providers, we thought of storing them with one database table or two of them.
We chose the implementation with one table, the **User** table. Basically, the class User has all the data a consumer or provider can have. Some of this information is optional, for example consumers don't have a store name. For this purpose we used the built-in data type **Option**. With this technique we also achieved storing those two types of users in different ways, and retrieve them back as they are. The only way of differentiating them, is via a parameter called 'category', which states if a user is indeed a consumer or not.
We chose this method because usernames are unique among **all** users, so to find out if there is a duplicate, we only need to search one database table.
The downside to this implementation is the extra work you have to do if you need only consumers/providers that match some filter. We have to redefine the model functions on the objects **Consumer** and **Provider**. These objects are only used to make the access to an specific user type easier.
This downside is solved if you use 2 database tables, one for consumers and one for providers. But if you want to check the authenticity of an username, you need to check two databases. 

Consumers and providers are located in some specific **Locations**, represented by a name, and a simple *(x, y)*. So, if a consumer is inside a provider "max distance" ([Manhattan] distance) , it will be able to buy items from that provider.

Provider can upload the **Items** they sell, which are unique based on the (provider_id, name). Every item has a price and description, and consumers can buy multiple of them.

When an order is created, consumers automatically pays it, and the provider receive the money. The order is available to be delivered after that. After it is delivered, the consumer is able to send a **Review** of the order and the provider, giving them from 1 to 10 stars, and a positive/negative comment. The reputation of the provided will be the mean between all star ratings given by the consumers in every order they had made.  

The RestfulAPIServer has all the `@get`, `@post` and `@postJson` functions which manage the incoming calls. Those functions insert, delete, or modify objects stored in the database directory. 

Every parameter on every class is accessible from every context, but only read-only, cause they are *val*. There are some properties that need to be re-writable, in those cases we made those variables **private/protected**, so the only way to access them is via functions defined inside those classes.

---

### Compile:
```
> cd grupo11_lab02_2019

> cd rapy

> sbt compile run
```

### Usage:
After the `sbt compile run` command, we should open a new terminal (Ctrl+N or Ctrl+Shift+N) to GET or POST things in the JSON database.

Using [HTTPIE], the commands would be:
##### GET:
```
> http http://localhost:4000/api/_OPTION_
```

Or

```
> http 0.0.0.0:4000/api/_OPTION_
```

##### POST
```
> http POST http://localhost:4000/api/_OPTION_ STR="val" INT:=val LIST:='[{"k1":"v1", "k2":v2}]'
```

Or

```
> http POST 0.0.0.0:4000/api/_OPTION_ STR="val" INT:=val LIST:='[{"k1":"v1", "k2":v2}]'
```

Where \_OPTION_ could be:

* locations

* consumers

* providers

* items

* orders

* reviews

And the values STR, INT, and LIST are the arguments to create or modify those objects.

### Example
```

// Creates the consumer location

> http POST 0.0.0.0:4000/api/locations name="Consumer1_house" coordX:=100 coordY:=100

  

// Creates the provider location

> http POST 0.0.0.0:4000/api/locations name="Provider1_store_location" coordX:=500 coordY:=700

  

// Creates the consumer

> http POST 0.0.0.0:4000/api/consumers username="Consumer1" locationName="Consumer1_house"

  

// Creates the provider

> http POST 0.0.0.0:4000/api/providers username="Provider1" storeName="Provider1_store" locationName="Provider1_store_location" maxDeliveryDistance:=1500

  

// Creates the item

> http POST 0.0.0.0:4000/api/items name="Item1" description="Nice_Item1" price:=35 providerUsername="Provider1"

  

// Creates the order

> http POST 0.0.0.0:4000/api/orders providerUsername="Provider1" consumerUsername="Consumer1," items:='[{"name":"Item1", "amount":10}]'

```

  
  

[Scala]:https://docs.scala-lang.org/tour/tour-of-scala.html

[HTTPIE]:https://httpie.org

[manhattan]:https://en.wikipedia.org/wiki/Taxicab_geometry