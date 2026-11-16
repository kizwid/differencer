general difference engine
compares 2 sorted sets
but as iterators
so each should contain only unique items
and each should be sorted with the same comparator
however for large datasets we cannot use a Set collection
so we use an Iterator instead and check that each iterator keeps heading in the same direction
and warn on the occurance of duplicates

the purpose is to quickly stream 2 sources of data and produce a stream of differences


Use Cases

1) differences between to SortedSets, where the pre-requisites are
    a) each set of data has a unique primary key
    b) each data set is sorted by the same unique primary key
   this is the most efficient way to compare data, since we only need to keep 2 records in memory at any one time

2) differences between 2 unsorted collections of data where the only pre-requisites are
    a) each collection of data has a natural key which has high cardinality such that when the data is sorted by natural key
       and some other value(s) it tends to be unique
   this is a much more expensive way to compare data since it will require pre-processing (sorting) and identifying duplicates.


Options

output destination (file, console, database, socket, etc)
format of output (csv, xml, json, text)
style of output (patch, rowPerKey, rowPerField)


#upgrade spring/java/junit

# for this PowerShell window only:
$Env:PATH -split ';'
$Env:JAVA_HOME='C:\Program Files (x86)\Java\jdk1.8.0_202'
$Env:JAVA_HOME='C:\Users\kevsa\.jdks\corretto-11.0.27'
$Env:JAVA_HOME='C:\Users\kevsa\.jdks\corretto-21.0.7'
$Env:PATH = $Env:JAVA_HOME + '\bin;' + $Env:PATH
$Env:PATH -split ';'
java -version

.\gradlew clean build

# for this PowerShell window only:
$Env:ORG_GRADLE_JAVA_HOME = 'C:\Users\kevsa\Dev\tools\jdk-17.0.7'
$Env:ORG_GRADLE_JAVA_HOME = 'C:\Users\kevsa\.jdks\corretto-21.0.7'
# then simply:
#.\gradlew wrapper --gradle-version 8.14.3 --distribution-type all
./gradlew wrapper --gradle-version 9.0.0 --distribution-type all

.\gradlew wrapper --gradle-version 4.8 --distribution-type all
.\gradlew wrapper --gradle-version 8.14.3 --distribution-type all

.\gradlew --stop
Remove-Item -Recurse -Force "$env:USERPROFILE\.gradle\caches"
