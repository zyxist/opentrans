OpenTrans
---------

This is my BSc. thesis project that aims to create a free
public transport simulator. It is based on my earlier work
created as a university project as well which served for
me as a prototype.

Requirements
------------

* Java 7
* Maven 3
* [Helium library](http://github.com/zyxist/helium)

Structure
---------

The general mechanics is provided by Visitons library which
serves as a simulation engine. The library provides:

* Visualization services
* Agent-based simulation engine
* Network structure representation
* Track object management

In addition, it defines the concept of transport simulation
project, and provides I/O operations that allow to read and
save the simulation state.

The actual application can be found in `opentrans-lightweight`
directory. It is written in Swing, using a custom application
mini-framework built around Google Guice, Google Guava and
other tools.

License and authors
-------------------

The software is available as a free software under the terms
of GNU Lesser General Public License 3.

(c) Invenzzia Group 2011-2013

* Author: Tomasz Jędrzejewski

The code does not contain any third party contributions,
especially from the prototype version, where other people
were also originally involved in creating it.