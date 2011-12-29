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
* NetBeans Platform
* http://github.com/zyxist/java-utils

Structure
---------

The general mechanics is provided by ViSiToNs library which
serves as a simulation engine. Most of the code is independent
from NBP, although there are some bundles in the .nbp package
using mostly the Nodes API.

ViSiToNs provides:

* Visualization services
* Agent-based simulation engine
* Network structure representation
* Track object management

In addition, it defines the concept of transport simulation
project, and provides I/O operations that allow to read and
save the simulation state.

The actual application is written with NetBeans Platform and
contains both a network editor, and the simulation runner.

License and authors
-------------------

The software is available as a free software under the terms
of GNU Lesser General Public License 3.

(c) Invenzzia Group 2011-2012

* Author: Tomasz Jędrzejewski

The code does not contain any third party contributions,
especially from the prototype version, where other people
were also originally involved in creating it.
