package parser

import lexicalAnalyzer.Lexer
import utils.ClassType
import utils.Token

class Parser {
    interface LexicalAnalyzerProvider {
        fun nextToken(): Token?

        fun returnToken(token: Token)
    }

    private val tokenBuffer = mutableListOf<Token>()
    val consumedTokens = mutableListOf<Token>()
    private lateinit var lexer: Lexer
    private val type = listOf("boolean", "int", "real", "string")

    fun main() {
        lexer = Lexer("./input/entrada10.txt")
        init()
        consumedTokens.forEach {
            println("Value: ${it.value} - line: ${it.line}")
        }
    }

    private fun init() {
        var token = lexer.nextToken()
        if(token != null && token.value == "procedure") {
            consumedTokens.add(token)
           token = lexer.nextToken()
           if(token != null && token.value == "start") {
               consumedTokens.add(token)
               token = lexer.nextToken()
               if(token != null && token.value == "{") {
                   consumedTokens.add(token)
                   start()
                   token = lexer.nextToken()
                   if(token != null && token.value == "}") {
                       consumedTokens.add(token)
                       // The program has ended
                   }
               }
           }
        }
    }

    private fun start() {
        program()
    }

    private fun program() {
        statement()
    }

    private fun statement() {
        var token = lexer.nextToken()
        while(token != null) {
            if (token != null && token.value == "{") {
                consumedTokens.add(token)
                token = lexer.nextToken()
                if (token != null && token.value == "}") {
                    consumedTokens.add(token)
                    //return
                } else {
                    //statementList()
                    token = lexer.nextToken()
                    if (token != null && token.value == "}") {
                        consumedTokens.add(token)
                        //return
                    }
                }
            } else {
                if (token != null && token.value == "var") { // Var declaration
                    consumedTokens.add(token)
                    token = lexer.nextToken()
                    if (token != null && token.value == "{") {
                        consumedTokens.add(token)
                        typedVariable()
                        token = lexer.nextToken()
                        if (token != null && token.value == "}") {
                            consumedTokens.add(token)
                            //return
                        }
                    }
                } else if (token != null && token.value == "const") { // Const declaration
                    token = lexer.nextToken()
                    if (token != null && token.value == "{") {
                        typedConst()
                        token = lexer.nextToken()
                        if (token != null && token.value == "}") {
                            //return
                        }
                    }
                } else if (token != null && token.value == "read") { // Read function
                    readStatement()
                } else if (token != null && token.value == "print") { // Print function

                } else if (token != null && token.value == "while") {
                    whileFunction()
                }
                // Calls all the other things
                else {
                    //token = lexer.nextToken()
                    break
                }
            }
        }
    }

    private fun whileFunction() {
        var token = lexer.nextToken()
        if(token != null && token.value == "(") {
            //expression()
            token = lexer.nextToken()
            if(token != null && token.value == ")") {
                statement()
            }
        }
    }

    private fun readStatement() {
        var token = lexer.nextToken()
        if(token != null && token.value == "(") {
            expRead()
            token = lexer.nextToken()
            if(token != null && token.value == ")") {
                return
            }
        }
    }

    private fun expRead() {
        var token = lexer.nextToken()
        if (token != null && token.type.type == ClassType.ID) {
            token = lexer.nextToken()
            if (token != null && token.value == ",") {
                expRead()
            } else if (token != null) {
                lexer.returnToken(token)
            }
            return
        } // Add here Array and Struct usages
    }

    private fun typedConst() {
        //TODO("Not yet implemented")
    }

    private fun typedVariable() {
        var token = lexer.nextToken()
        if(token != null && type.contains(token.value)) {
            consumedTokens.add(token)
            variable()
            token = lexer.nextToken()
            if(token != null && token.value == ";") {
                consumedTokens.add(token)
                token = lexer.nextToken()
                if(token != null && type.contains(token.value)) { // Has other variable types
                    lexer.returnToken(token)
                    typedVariable()
                } else if(token != null && token.value == "}") { // Var block has ended
                    consumedTokens.add(token)
                    return
                }
            }
        }
    }

    private fun variable() {
        variableDeclarator()
        var token = lexer.nextToken()
        if(token != null && token.value == ",") {
            consumedTokens.add(token)
            variable()
        } else if(token != null) {
            lexer.returnToken(token)
        }
        return
    }

    private fun variableDeclarator() {
        var token = lexer.nextToken()
        if(token != null && token.type.type == ClassType.ID) {
            consumedTokens.add(token)
            token = lexer.nextToken()
            if(token != null) {
                when {
                    token.value == "["-> {
                        token = lexer.nextToken()
                        if(token != null && (token.type.type == ClassType.ID || token.value == "true" ||
                                    token.value == "false" || token.type.type == ClassType.NUMBER ||
                                    token.type.type == ClassType.STRING)) {
                            token = lexer.nextToken()
                            if(token != null && token.value == "]") { // Array type
                                token = lexer.nextToken()
                                if (token != null && token.value == "[") { // Matrix type
                                    token = lexer.nextToken()
                                    if(token != null && (token.type.type == ClassType.ID || token.value == "true" ||
                                                token.value == "false" || token.type.type == ClassType.NUMBER ||
                                                token.type.type == ClassType.STRING)) {
                                        token = lexer.nextToken()
                                        if(token != null && token.value == "]") { // Array type
                                            token = lexer.nextToken()
                                            if(token != null && token.value == "=") {
                                                token = lexer.nextToken()
                                                if(token != null && token.value == "{") {
                                                    varArgs()
                                                    token = lexer.nextToken()
                                                    if(token != null && token.value == "}") {
                                                        return
                                                    }
                                                }
                                            } else if(token != null) {
                                                lexer.returnToken(token)
                                            }
                                            return
                                        }
                                    }
                                } else {
                                    token = lexer.nextToken()
                                    if(token != null && token.value == "=") {
                                        token = lexer.nextToken()
                                        if(token != null && token.value == "{") {
                                            varArgs()
                                            token = lexer.nextToken()
                                            if(token != null && token.value == "}") {
                                                return
                                            }
                                        }
                                    } else if(token != null) {
                                        lexer.returnToken(token)
                                    }
                                    return
                                }
                            }
                        }
                    }
                    token.value == "=" -> {
                        consumedTokens.add(token)
                        token = lexer.nextToken()
                        if(token != null && (token.value == "local" || token.value == "global")) { // VariableScopeType . Identifier
                            consumedTokens.add(token)
                            token = lexer.nextToken()
                            if(token != null && token.value == ".") {
                                consumedTokens.add(token)
                                token = lexer.nextToken()
                                if(token != null && token.type.type == ClassType.ID) {
                                    consumedTokens.add(token)
                                }
                            }
                        } else if(token != null && token.type.type == ClassType.ID) {
                            consumedTokens.add(token)
                            var lookaheadtoken = lexer.nextToken()
                            if(lookaheadtoken != null && token.value == ".") { // Struct Usage
                                consumedTokens.add(token)
                                token = lexer.nextToken()
                                if(token != null && token.type.type == ClassType.ID) {
                                    consumedTokens.add(token)
                                }
                            } else if(lookaheadtoken != null && token.value == "(") { // Call function
                                consumedTokens.add(token)
                                //Args()
                                token = lexer.nextToken()
                                if(token != null && token.value == ")") {
                                    consumedTokens.add(token)
                                }
                            } else if(lookaheadtoken != null) { // Expression
                                consumedTokens.removeAt(consumedTokens.size - 1)
                                lexer.returnToken(lookaheadtoken)
                                lexer.returnToken(token)
                                expression()
                            }
                        } else if(token != null && (token.type.type == ClassType.NUMBER ||
                                    token.type.type == ClassType.STRING || token.value == "true" || token.value == "false")) {
                            lexer.returnToken(token)
                            expression()
                        }
                    }
                    else -> {
                        lexer.returnToken(token)
                    }
                }
            }
        }
    }

    private fun varArgs() {
        //TODO("Not yet implemented")
    }

    private fun expression() {
        orExpression()
    }

    private fun orExpression() {
        andExpression()
        var token = lexer.nextToken()
        if(token != null && token.value == "||") {
            consumedTokens.add(token)
            orExpression()
        } else if(token != null) {
            lexer.returnToken(token)
        }
    }

    private fun andExpression() {
        equalityExpression()
        var token = lexer.nextToken()
        if(token != null && token.value == "&&") {
            consumedTokens.add(token)
            andExpression()
        } else if(token != null) {
            lexer.returnToken(token)
        }
    }

    private fun equalityExpression() {
        compareExpression()
        var token = lexer.nextToken()
        if(token != null && token.value == "==") {
            consumedTokens.add(token)
            equalityExpression()
        } else if(token != null && token.value == "!=") {
            consumedTokens.add(token)
            equalityExpression()
        } else if(token != null) {
            lexer.returnToken(token)
        }
    }

    private fun compareExpression() {
        addExpression()
        var token = lexer.nextToken()
        if(token != null && token.value == "<") {
            consumedTokens.add(token)
            compareExpression()
        } else if(token != null && token.value == ">") {
            consumedTokens.add(token)
            compareExpression()
        } else if (token != null && token.value == "<=") {
            consumedTokens.add(token)
            compareExpression()
        } else if (token != null && token.value == ">=") {
            consumedTokens.add(token)
            compareExpression()
        } else if (token != null) {
            lexer.returnToken(token)
        }
    }

    private fun addExpression() {
        multiplicationExpression()
        var token = lexer.nextToken()
        if(token != null && token.value == "+") {
            consumedTokens.add(token)
            addExpression()
        } else if(token != null && token.value == "-") {
            consumedTokens.add(token)
            addExpression()
        } else if(token != null) {
            lexer.returnToken(token)
        }
    }

    private fun multiplicationExpression() {
        unaryExpression()
        var token = lexer.nextToken()
        if(token != null && token.value == "*") {
            consumedTokens.add(token)
            multiplicationExpression()
        } else if(token != null && token.value == "/") {
            consumedTokens.add(token)
            multiplicationExpression()
        } else if(token != null) {
            lexer.returnToken(token)
        }
    }

    private fun unaryExpression() {
        var token = lexer.nextToken()
        if(token != null && token.value == "!") {
            consumedTokens.add(token)
            unaryExpression()
        } else if(token != null) {
            lexer.returnToken(token)
            primaryExpression()
        }
    }

    private fun primaryExpression() {
        var token = lexer.nextToken()
        if(token != null && token.value == "(") {
            consumedTokens.add(token)
            expression()
            token = lexer.nextToken()
            if(token != null && token.value == ")") {
                consumedTokens.add(token)
                return
            }
        } else if(token != null && (token.type.type == ClassType.ID || token.value == "true" || token.value == "false" ||
                    token.type.type == ClassType.NUMBER || token.type.type == ClassType.STRING)) {
            consumedTokens.add(token)
            return
        }
    }
}