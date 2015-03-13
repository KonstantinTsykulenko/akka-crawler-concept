package com.tsykul.crawler

import scalaz._
import Scalaz._

class TreeSpec extends BaseSpec {
  test("Should be able to create tree and insert/replace nodes") {
    val tree: Tree[String] = "A".node("B".leaf, "C".leaf)
    val tree2 = tree.loc.insertDownLast("D".leaf).toTree

    assert(tree.loc.find(_.getLabel == "A").nonEmpty)
    assert(tree.loc.find(_.getLabel == "B").nonEmpty)
    assert(tree.loc.find(_.getLabel == "C").nonEmpty)
    assert(tree.loc.find(_.getLabel == "D").isEmpty)
    assert(tree2.loc.find(_.getLabel == "D").nonEmpty)

    val tree3 = tree2.loc.find(_.getLabel == "D").map(_.setTree("E".leaf).toTree).getOrElse(tree2)
    assert(tree2.loc.find(_.getLabel == "E").isEmpty)
    assert(tree2.loc.find(_.getLabel == "D").nonEmpty)
    assert(tree3.loc.find(_.getLabel == "E").nonEmpty)
    assert(tree3.loc.find(_.getLabel == "D").isEmpty)
  }
}
