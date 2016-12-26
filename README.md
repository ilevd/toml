# toml

A Clojure [TOML](https://github.com/toml-lang/toml) wrapper based on Java [toml4j](https://github.com/mwanji/toml4j) library.
The tests are from [clj-toml](https://github.com/lantiga/clj-toml).

Despite the fact the library is pretty fast, cause it's written in Java, I'd reccomend you to visit [clj-toml](https://github.com/lantiga/clj-toml) -
 it looks more solid than [toml4j](https://github.com/mwanji/toml4j) and it's written in pure Clojure using [Instaparse](https://github.com/Engelberg/instaparse).


## Installation

#### Leiningen

[![Clojars Project](https://clojars.org/toml/latest-version.svg)](http://clojars.org/toml)

If you don't like extra clicks, just copy it:
```
[toml "0.1.0-SNAPSHOT"]
```

## Usage

```clojure
(:require [toml.core :as toml])

(toml/read "a = 10\nb = 20")
=> {"a" 10, "b" 20}
```

You can keywordize result keys, just pass ``:keywordize``

```clojure
(toml/read "a = 10\nb = 20" :keywordize)
=> {:a 10, :b 20}
```

More complex example, assuming you have example.toml:
```
# This is a TOML document.

title = "TOML Example"

[owner]
name = "Tom Preston-Werner"
dob = 1979-05-27T07:32:00-08:00 # First class dates

[database]
server = "192.168.1.1"
ports = [ 8001, 8001, 8002 ]
connection_max = 5000
enabled = true

[servers]

  # Indentation (tabs and/or spaces) is allowed but not required
  [servers.alpha]
  ip = "10.0.0.1"
  dc = "eqdc10"

  [servers.beta]
  ip = "10.0.0.2"
  dc = "eqdc10"

[clients]
data = [ ["gamma", "delta"], [1, 2] ]

# Line breaks are OK when inside arrays
hosts = [
  "alpha",
  "omega"
]

[[storages]]
name = "Redis"
priority = 10

[[storages]]
name = "Memcached"
priority = 5
```

Just do it:
```clojure
(toml.core/read (slurp "./resources/example.toml") :keywordize)
=> 
{:owner {:dob #inst"1979-05-27T15:32:00.000-00:00", :name "Tom Preston-Werner"},
 :storages [{:name "Redis", :priority 10} {:name "Memcached", :priority 5}],
 :database {:server "192.168.1.1", :connection_max 5000, :ports [8001 8001 8002], :enabled true},
 :servers {:alpha {:ip "10.0.0.1", :dc "eqdc10"}, :beta {:ip "10.0.0.2", :dc "eqdc10"}},
 :clients {:data [["gamma" "delta"] [1 2]], :hosts ["alpha" "omega"]},
 :title "TOML Example"}
```

## License

ilevd © 2016-2017

