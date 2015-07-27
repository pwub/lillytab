# LillyTab

## Synopsis

LillyTab is a modular ABox consistency checker for the description logic SHOF(D).

To put it less formally, *LillyTab* is an *OWL2* reasoner (but with support for only a limited subset of OWL2). 
A primary goal of LillyTab is to be extensible and inspectable, meaning it gives you easy access to the internal
workings of its tableau state.

## Current Features

* documented access to ABox, TBox and RBox representation internals
* OWLAPI (3, 4 coming soon) integration
* modular architecture (add and replace tableau completers via API)
* support for the DL SHOF(D) (which is OWL/DL minus qualified number restrictions and inverse roles)
* various basic optimizations 
  * lazy unfolding
  * lazy saving (copy on write)
  * input term rewriting
  * dependency directed backtracking
* tracing and logging
