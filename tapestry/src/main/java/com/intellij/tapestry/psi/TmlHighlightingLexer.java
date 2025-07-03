package com.intellij.tapestry.psi;

import com.intellij.lexer.BaseHtmlLexer;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexey Chmutov
 */
public class TmlHighlightingLexer extends BaseHtmlLexer {

  private static final TokenSet CUSTOM_ATTRIBUTE_TOKENS = TokenSet.create(TelTokenTypes.TAP5_EL_CONTENT);

  public TmlHighlightingLexer() {
    super(new TmlLexer(), false);
  }

  @Override
  protected @NotNull TokenSet createAttributeEmbedmentTokenSet() {
    return TokenSet.orSet(super.createAttributeEmbedmentTokenSet(), CUSTOM_ATTRIBUTE_TOKENS);
  }
}

