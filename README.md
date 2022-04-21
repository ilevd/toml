# toml

TOML is a configuration format, similar to JSON, but much more human readable. It's like YAML, but easier to write. You can read about it [here](https://npf.io/2014/08/intro-to-toml/).

This is a Clojure [TOML](https://github.com/toml-lang/toml) wrapper based on Java [toml4j](https://github.com/mwanji/toml4j) library.
The tests are from [clj-toml](https://github.com/lantiga/clj-toml).


## Installation

#### Leiningen

[![Clojars Project](https://clojars.org/toml/latest-version.svg)](http://clojars.org/toml)

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
```toml
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
(toml/read (slurp "./resources/example.toml") :keywordize)
=> 
{:owner {:dob #inst"1979-05-27T15:32:00.000-00:00", :name "Tom Preston-Werner"},
 :storages [{:name "Redis", :priority 10} {:name "Memcached", :priority 5}],
 :database {:server "192.168.1.1", :connection_max 5000, :ports [8001 8001 8002], :enabled true},
 :servers {:alpha {:ip "10.0.0.1", :dc "eqdc10"}, :beta {:ip "10.0.0.2", :dc "eqdc10"}},
 :clients {:data [["gamma" "delta"] [1 2]], :hosts ["alpha" "omega"]},
 :title "TOML Example"}
```

You can also generate TOML doc (for simple cases by now):

```clojure
(toml/write {:owner    {:name "Tom Preston-Werner"},
             :database {:server "192.168.1.1", :connection_max 5000, :port 8002, :enabled true},
             :servers  {:alpha {:ip "10.0.0.1", :dc "eqdc10"}, :beta {:ip "10.0.0.2", :dc "eqdc10"}},
             :title    "TOML Example"})
=>
"title = \"TOML Example\"
 
 [owner]
 name = \"Tom Preston-Werner\"
 
 [database]
 server = \"192.168.1.1\"
 connection_max = 5000
 port = 8002
 enabled = true
 
 [servers.alpha]
 ip = \"10.0.0.1\"
 dc = \"eqdc10\"
 
 [servers.beta]
 ip = \"10.0.0.2\"
 dc = \"eqdc10\"
 "

```


For more information, please, visit [toml4j](https://github.com/mwanji/toml4j) library.

## License

ilevd © 2016-2018

Distributed under the MIT License.
