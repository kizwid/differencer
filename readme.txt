general difference engine
compares 2 sorted sets
but as iterators
so each should contain only unique items
and each should be sorted with the same comparator
however for large datasets we cannot use a Set collection
so we use an Iterator instead and check that each iterator keeps heading in the same direction
and warn on the occurance of duplicates

the purpose is to quickly stream 2 sources of data and produce a stream of differences