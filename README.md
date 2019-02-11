## Question 1
`sbt "run <absolute_path_to_file>"` will run the program. 
Where `<absolute_path_to_file>` is file with all the expressions to run.

## Question 3
#### Problems with current solution.

* Threads are actually not started
* If we start threads immediately after creation results will be 
unpredictable
* Even if we start threads just before the call to `t.join()` it won't 
work in parallel, but because of the `t.join` - each new transformation
 thread will start only after previous one is finished
* There is a good chance that even this kind of execution wouldn't end
 up correctly because there is no synchronization for `data` field, 
 so some results could be cached locally in CPU and some of the 
 transformations could be lost


#### Solution 1 (Index based Thread)

##### Description

Each started thread can take an `index` of original `List<String> data` as 
a parameter and apply full list of transformations to `String` at 
that particular `index` and then assign it back.
I would also use `AtomicReferenceArray` instead of `List<String>`
to make sure that results of transformation will be available 
immediately after `Thread` is terminated.

##### Pros

* Simple
* Almost no need for synchronization
* Scalable: number of threads can always be configured
* No 3rd party libraries

##### Cons

* Will become complicated very fast with new features added

#### Solution 2 (Akka, Akka Streams) 

##### Description

Use library like Akka `ActorSystem` to create a transformation pipeline, where each `String`
  is an event and each transformation is a processing step.
Each `String` from `data` is sent to an actor that applies all 
the transformations on that string and returns it back.

(For even higher abstraction `AkkaStreams` can be used)

##### Pros

* High level of abstraction: easy to reason
* High level of scalability: number of actors/stages can always be added
* Easily extensible: if something changes in the processing pipeline we can easily change the message flow 
  
#### Cons

* Probably to much to solve a simple thing like in this exercise

## Question 4 

#### Cache Features

* Concurrent access for reads: we can back implementation of
 cache by `ConcurrentHasMap`
* On multiple concurrent misses for the same key we should not repeat 
call to the source, but rather wait for the first call to finish and 
buffer all the rest
* Miss/hit ratio stats: we need to keep track of miss/hit ratio
* Configurable cache-eviction policy: since we don't know how 
our cache will be used we need to be able to configure eviction policy 
to optimize hit/miss ratio stats
* Configurable cache size: same goes for cache size
* Time-based cache invalidation policy: can't think of anything else, 
since we don't support write operation we don't know when to update entries
* Warmups upon start: can keep logs of cache access and 
in case of restart replay them a couple of times  

#### Question 5

Both issues can be fixed by using a distributed cache like `memcached`, 
frontend servers can use `memcached` instances instead of local cache 
and every time backend server generates new data for keys that are 
stored in `memcached` it updates `memcached`. 

If we use Cassandra local cache and distributed cache is not needed at all
since Cassandra has built in configurable cache that stores rows in memory.





