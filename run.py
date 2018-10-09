#! /usr/bin/python

from pyama.configuration import Configuration
from pyama.processor import Processor
from pyama.snippet import MdSnippetWriter, SnippetReader
from pyama.regexhandler import RegexHandler
from pyama.linenumberer import LineNumberer
from pyama.lineskipperhandler import LineSkipper

MD = Configuration().file(r".*\.md$").exclude("target").handler(MdSnippetWriter(), SnippetReader(), RegexHandler(runpass=[4]),
                                              LineNumberer(runpass=[5]), LineSkipper(runpass=[3]))
JAM = Configuration().file(r".*\.java$").exclude("target").handler(SnippetReader())
configs = [MD, JAM]

Processor(configs, "**/*.*").process()
