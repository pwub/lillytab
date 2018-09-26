# LillyTab

## Synopsis

LillyTab is a modular ABox consistency checker for the description logic SHOIF(D).

To put it less formally, *LillyTab* is an *OWL2* reasoner (but with support for only a limited subset of OWL2). 
A primary goal of LillyTab is to be extensible and inspectable, meaning it gives you easy access to the internal
workings of its tableau state.

This can be quite useful because the final (= saturated) state tableau algorithm exposes some
interesting information about the ontology that can be used for further processing. 

However, With most DL reasoners there is usually no documented way to get to this information
Also, it is often not clear, if part of the information has been left out because of reasoner optimizations.
LillyTab provides a solution for such problems (provided that you don't need inverse role or number restrictions).

LillyTab is also a great DL teaching tool because of the simplicity of inspecting and printing reasoner state.

## Current Features

* documented access to ABox, TBox and RBox representation internals
* modular architecture (add and replace tableau completers via API)
* OWLAPI 4 integration (since version 1.11)
* support for the DL SHOIF(D) (which is OWL/DL minus qualified number restrictions)
* various basic optimizations 
  * lazy unfolding
  * lazy saving (copy on write)
  * input term rewriting
  * dependency directed backtracking

Caveat Emptor:
Despite optimizations Lillytab is not performing well when expanding union terms and is thus not exceptionally well suited for some ontologies. Use with care.
