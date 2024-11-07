# chef

Chef reads recipe and in each step executes task.

I use it to rebuild my system environments. (See example-of-recipe.edn)


## Why
I wanted to have declaration of required packages for my system and to be able to install them with one command.
Previously I've done this with bash scripts but the maintance got tedious, so I built this.

### Why not [insert some other tool]
Idk, nothing really felt right and I like edn format and clojure, so...

I might evolve this project to something more interesting over the time. I'm thinking of using this for simple ci/cd builder in the future.


## Usage

```
chef [options] recipe.edn

Options:
  -v, --verbose
  -h, --help
```

## Dev usage

Run the project directly, via `:main-opts` (`-m chef.chef`):

    $ clojure -M:run-m example-of-recipe.edn

Run the project's tests:

    $ clojure -T:build test
    
Run the project's uberjar build:

    $ clojure -T:build uberjar
    
Run the project's native image build:

    $ clojure -T:build compile-native

Run the project's CI pipeline and build an uberjar and native image:

    $ clojure -T:build ci

Run that uberjar:

    $ java -jar target/chef-0.1.0-SNAPSHOT.jar example-of-recipe.edn

Run the native image:

    $ ./target/chef example-of-recipe.edn
