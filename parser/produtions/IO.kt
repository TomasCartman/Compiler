package parser.produtions

import parser.exceptions.NextTokenNullException
import parser.exceptions.ParserException
import parser.produtions.Array.Companion.arrayUsage
import parser.produtions.Delimiters.Companion.closingParenthesis
import parser.produtions.Delimiters.Companion.comma
import parser.produtions.Delimiters.Companion.isNextTokenComma
import parser.produtions.Delimiters.Companion.openingParenthesis
import parser.produtions.Delimiters.Companion.semicolon
import parser.produtions.Struct.Companion.structUsage
import parser.produtions.VarDeclaration.Companion.identifier
import parser.produtions.VarDeclaration.Companion.isTokenIdentifier
import parser.produtions.VarDeclaration.Companion.isTokenLiteral
import parser.produtions.VarDeclaration.Companion.literal
import parser.produtions.VarDeclaration.Companion.primaryStringListName
import parser.utils.Utils
import parser.utils.Utils.Companion.hasNextToken
import parser.utils.Utils.Companion.nextToken
import parser.utils.Utils.Companion.peekNextToken
import parser.utils.Utils.Companion.removeLastReadTokenAndPutBackInTokenList
import utils.Token

class IO {
    companion object {
        fun readStat(tokenBuffer: MutableList<Token>) {
            tokenBuffer.nextToken() // Consume read
            openingParenthesis(tokenBuffer)
            expRead(tokenBuffer)
            closingParenthesis(tokenBuffer)
            semicolon(tokenBuffer)
        }

        private fun expRead(tokenBuffer: MutableList<Token>) {
            if (tokenBuffer.hasNextToken() && isTokenIdentifier(tokenBuffer.peekNextToken())) {
                identifier(tokenBuffer)
                if (tokenBuffer.hasNextToken() &&tokenBuffer.peekNextToken().value == "[") {
                    tokenBuffer.removeLastReadTokenAndPutBackInTokenList()
                    arrayUsage(tokenBuffer)
                } else if(tokenBuffer.hasNextToken() &&tokenBuffer.peekNextToken().value == ".") {
                    tokenBuffer.removeLastReadTokenAndPutBackInTokenList()
                    structUsage(tokenBuffer)
                }
            } else {
                throw ParserException(tokenBuffer.peekNextToken().line, tokenBuffer.peekNextToken(),
                    listOf("Identifier"))
            }

            if (tokenBuffer.hasNextToken() &&isNextTokenComma(tokenBuffer.peekNextToken())) {
                comma(tokenBuffer)
                expRead(tokenBuffer)
            }
        }

        fun printStat(tokenBuffer: MutableList<Token>) {
            tokenBuffer.nextToken() // Consume print
            openingParenthesis(tokenBuffer)
            expPrint(tokenBuffer)
            closingParenthesis(tokenBuffer)
            semicolon(tokenBuffer)
        }

        private fun expPrint(tokenBuffer: MutableList<Token>) {
            try {
                if (tokenBuffer.hasNextToken() &&isTokenIdentifier(tokenBuffer.peekNextToken())) {
                    identifier(tokenBuffer)
                    if (tokenBuffer.hasNextToken() &&tokenBuffer.peekNextToken().value == "[") {
                        tokenBuffer.removeLastReadTokenAndPutBackInTokenList()
                        arrayUsage(tokenBuffer)
                    } else if(tokenBuffer.hasNextToken() &&tokenBuffer.peekNextToken().value == ".") {
                        tokenBuffer.removeLastReadTokenAndPutBackInTokenList()
                        structUsage(tokenBuffer)
                    }
                } else if(tokenBuffer.hasNextToken() &&isTokenLiteral(tokenBuffer.peekNextToken())) {
                    literal(tokenBuffer)
                } else {
                    throw ParserException(tokenBuffer.peekNextToken().line, tokenBuffer.peekNextToken(),
                        primaryStringListName)
                }
            } catch (e: NextTokenNullException) {
                Utils.throwParserError(primaryStringListName, tokenBuffer)
            }

            if (tokenBuffer.hasNextToken() && isNextTokenComma(tokenBuffer.peekNextToken())) {
                comma(tokenBuffer)
                expPrint(tokenBuffer)
            }
        }

        fun isNextTokenReadStat(token: Token): Boolean = token.value == "read"

        fun isNextTokenPrintStat(token: Token): Boolean = token.value == "print"
    }
}